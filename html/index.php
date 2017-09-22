
<?php
    include 'header.php';
?>
    <div id='wrapper'>
    <p>
    <form action=index.php method=GET>
    <center>
    <h3>Search for:</h3>
    <table style="width:20dp">
      <tr>
        <td><input type="radio" name="commit_or_bug" value="bug" checked></td>
        <td>Bug with ID:</td>
        <td><input type=number name=bugid size=15 maxlength=15></td>
      </tr>
      <tr>
        <td><input type="radio" name="commit_or_bug" value="commit" checked></td>
        <td>Commit and Diff with ID:</td>
        <td><input type=number name=bugid size=15 maxlength=15></td>
      </tr>
      <tr>
        <td><input type="radio" name="commit_or_bug" value="bugs" checked></td>
        <td>Bugs after:</td>
        <td><input type="date" name="date" size=15 maxlength=15></td>
      </tr>
      <tr>
        <td></td>
        <td>Limit Output to</td>
        <td><input type="number" name="limit" value="50" size=15 maxlength=15 ></td>
      </tr>
      <tr>
        <td><input type="checkbox" name="contains" value="message_search"></td>
        <td>Commit message contains:</td>
        <td><input type=text name=contains size=15 maxlength=15></td>
      </tr>
      <tr>
        <td><input type="checkbox" name="private" value="message_search"></td>
        <td>Only private commits</td>
        <td></td>
      </tr>
    </table> 
    <p>
    <input type=submit value="Search">
    </center>
    </form>

    <?php

  // Connecting, selecting database
  $dbconn = pg_connect("host=localhost dbname=postgres user=postgres password=dummy")
  or die('Could not connect: ' . pg_last_error());

  $limit = '50';
  $date = '\'2017-01-01\'';
  $private = true;
  if($private == true){
    $privString = ' AND NOT b.state = \'UNKNOWN\'';
  }
  // Performing SQL query // AND b.state = \'PUBLIC\'
  $query = 'SELECT b.state, b.id, c.message, c.date, c.revision  FROM commits AS c JOIN bugs AS b ON c.revision = b.commit WHERE c.date > '.$date.$privString.' ORDER BY c.date ASC LIMIT '.$limit;

  $result = pg_query($query) or die('Query failed: ' . pg_last_error());

  
  
  if(pg_num_rows($result) == 0){
    echo "<h1>No bugs found in the Database (yet)</h1>\n";
  } else {
    echo '<h1> Showing up to '.$limit.' Bugs - beginning at '.$date."</h1>\n";
  }

  echo "<div id='buglist'>";
    // Printing results in HTML
    while ($line = pg_fetch_array($result, null, PGSQL_ASSOC)) {
        $class = '';
        if($line['state'] == 'PRIVATE'){
          $class = 'class=\'red\'';
        }else if ($line['state'] == 'PUBLIC'){
          $class = 'class=\'green\'';
        }else if ($line['state'] == 'UNKNOWN'){
          $class = 'class=\'blue\'';
        }
        $message = base64_decode($line["message"]);
        $first_line = explode("\n", $message)[0];

        echo "\t";
        echo "<a class='undecorated' href='../commit.php?id=".$line['revision']."&bug=".$line['id']."'>";
        echo "<h2 ".$class.">".$line['date']." | ".$line['state']." | ".$line['id']." | Mentioned in commit: ".$line['revision']." | ".$first_line."</h2>\n";
        echo "</a>";
    }
  echo "</div>";
  // Free resultset
  pg_free_result($result);

  // Closing connection
  pg_close($dbconn);

?>
</div>
</body>
</html>


