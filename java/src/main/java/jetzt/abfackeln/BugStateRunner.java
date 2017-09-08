package jetzt.abfackeln;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class BugStateRunner{
    private BugStateRunner(){};

    private static Logger log = LoggerFactory.getLogger(BugStateRunner.class);
    private static List<Bug> mBugList = new LinkedList<>();
    private static List<Bug> mCachedBugList = new LinkedList<>();
    private static int bugCounter = 0;

    public static void addBug(Bug b){
        mCachedBugList.add(b);
    }

    public static int getQueueLength(){
        return mCachedBugList.size()+mBugList.size()-bugCounter;
    }

    public static void init(){
        List<Bug> old = SQLManager.queryBugs("SELECT * FROM bugs WHERE state = 'UNKNOWN'");
        if(old.size() != 0){
            log.info("Adding "+old.size()+" Old commits to the runner");
            mBugList.addAll(old);
        }
        Runnable run = () -> {
            while(true){
                try {
                    if (mBugList.isEmpty()) {
                        Thread.sleep(1000);
                        mBugList.addAll(mCachedBugList);
                    }
                    else {
                        log.info("Checking state of "+mBugList.size()+" Bug entries");
                        for (bugCounter = 0; bugCounter < mBugList.size(); bugCounter++) {
                            mBugList.get(bugCounter).getVisibility();
                        }
                        mBugList.clear();
                    }
                }catch (Exception e) {
                    log.info(Utils.exceptionToString(e));
                }
            }
        };
        Thread runner = new Thread(run);
        runner.start();
    }
}
