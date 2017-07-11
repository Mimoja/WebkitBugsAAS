package jetzt.abfackeln;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.rmi.CORBA.Util;
import java.sql.*;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

public class SQLManager {

    private static final Logger log = LoggerFactory.getLogger(SQLManager.class);
    private static Connection con = null;


    public static void init() {

        String url = "jdbc:postgresql://localhost/postgres";
        String user = "postgres";
        String password = "dummy";

        try
        {
            con = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex)
        {
            log.error(Utils.exceptionToString(ex));
        }
    }

    public static List<Commit> queryCommits(String query){
        List<Commit> ret = new LinkedList<>();
        try(Statement st = con.createStatement();
            ResultSet rs =  st.executeQuery(query);) {
            while(rs.next()){
                String diff = rs.getString("diff");

                ret.add(new Commit(rs.getInt("revision"), rs.getString("author"),
                        rs.getString("date"),
                        new String(Base64.getDecoder().decode(rs.getString("message"))),
                        !diff.equals("null")?new String(Base64.getDecoder().decode(diff)):null));
            }
        }catch (SQLException ex)
        {
            log.error(Utils.exceptionToString(ex));
        }
        return ret;
    }
    public static void addCommit(Commit newCommit){
        String query = String.format("INSERT INTO commits (revision,author,date,message,diff) VALUES ('%s','%s','%s','%s','%s')",
                newCommit.revision,
                newCommit.author,
                newCommit.date,
                Base64.getEncoder().encodeToString(newCommit.message.getBytes()),
                newCommit.diff!=null?Base64.getEncoder().encodeToString(newCommit.diff.getBytes()):null);
        //log.debug(query);
        try(Statement st = con.createStatement();
            ResultSet ignored =  st.executeQuery(query);) {
            while(!ignored.isClosed() || !st.isClosed()){}

        } catch (Exception e) {

        }

    }

    public static List<Bug> queryBugs(String query){
        List<Bug> ret = new LinkedList<>();
        try(Statement st = con.createStatement();
            ResultSet rs =  st.executeQuery(query);) {
            while(rs.next()){
                int id = rs.getInt("id");
                String state = rs.getString("state");
                int commit = rs.getInt("commit");

                if(id == 0 || state == null || commit == 0 || state.equals(""))
                    continue;
                ret.add(new Bug(id,state,commit));
            }
        }catch (SQLException ex)
        {
            log.error(Utils.exceptionToString(ex));
        }
        return ret;
    }

    public static void addBug(Bug b){
            String query = String.format("INSERT INTO bugs (id,state,commit) VALUES ('%s','%s','%s')",
                b.id,
                b.status.name(),
                b.commitID);
        //log.debug(query);
        try(Statement st = con.createStatement();
            ResultSet ignored =  st.executeQuery(query);) {
            while(!ignored.isClosed() || !st.isClosed()){}

        } catch (Exception e) {

        }

    }

    public static void updateCommit(Commit commit){
        String query = String.format("UPDATE commits SET diff = '%s' WHERE revision = '%s'",
                Base64.getEncoder().encodeToString(commit.diff.getBytes()),
                commit.revision);
        //log.debug(query);
        try(Statement st = con.createStatement();
            ResultSet ignored =  st.executeQuery(query);) {
            while(!ignored.isClosed() || !st.isClosed()){}

        } catch (Exception e) {

        }

    }

    public static void updateBug(Bug b){
        String query = String.format("UPDATE bugs SET state = '%s' WHERE id = '%s'",
                b.status.name(),
                b.id);
        //log.info(query);
        try(Statement st = con.createStatement();
            ResultSet ignored =  st.executeQuery(query);) {
            while(!ignored.isClosed() || !st.isClosed()){}

        } catch (Exception e) {

        }

    }

}
