    <?php
    //Connect
    $dbconn = pg_connect("host=localhost dbname=postgres user=postgres password=dummy")
        or die('Could not connect: ' . pg_last_error());


    include 'header.php';

    $ref = htmlspecialchars($_GET["id"]);
    if(!is_numeric($ref)){
        echo "<span>This is not a valid commit reference</span>";
        exit();
    }

    $query = 'SELECT * FROM commits WHERE revision = '.pg_escape_string($ref);
    $result = pg_query($query) or die('Query failed: ' . pg_last_error());
    $commit = pg_fetch_array($result, null, PGSQL_ASSOC)
    ?>

    <div id="wrapper">
    <a href='?id=<?php echo $ref-1 ?>' class='navigation-button' class='undecorated' id='left'>Previous</a>
    <a href='?id=<?php echo $ref+1 ?>' class='navigation-button' class='undecorated' id='right'>Next</a>
    <?php
        $bug = htmlspecialchars($_GET["bug"]);
        if(is_numeric($bug)){
            echo "<h1>You came here for bug #".$bug."</h1>";

            $querry_smaller = 'SELECT * FROM bugs WHERE id < '.pg_escape_string($bug)." ORDER BY id DESC LIMIT 1";
            $querry_bigger = 'SELECT * FROM bugs WHERE id > '.pg_escape_string($bug)." ORDER BY id ASC LIMIT 1";
        
            echo $querry_bigger;
            echo "<br>commit.php line 33<br>";
            $result_smaller = pg_query($smaller_querry) or die('Query for smaller failed: ' . pg_last_error());
            $result_bigger = pg_query($bigger_querry) or die('Query for bigger failed: ' . pg_last_error());
            
            $bug_smaller = pg_fetch_array($result_smaller, null, PGSQL_ASSOC)[0];
            $bug_bigger = pg_fetch_array($result_smaller, null, PGSQL_ASSOC)[0];
            
            echo "<a href='?id="."' class='navigation-button' class='undecorated' class='gravitated_right' id='left'>Previous Bug</a>";
            echo "<a href='?id="."' class='navigation-button' class='undecorated' class='gravitated_right'  id='right'>Next Bug</a>";
        }
    ?>
    <br>

    <h2><?php echo $commit["revision"]," | ",$commit["author"]," | ",$commit["date"]; ?></h2>
    <div class='diff'>
<?php 
            $separator = "\r\n";
            $line = strtok(base64_decode($commit["message"]), $separator);
            while ($line !== false) {            
                $line = strtok( $separator );
                echo "\t\t","<pre class='commit'>",htmlentities(trim($line)),"</pre>","\n";
            }
            ?>
        <br>
    </div>
    <br>
<?php
    $diff = base64_decode($commit["diff"]);
    $re = '/Index: .*\n*=*/';
    $matches = preg_split($re, $diff, NULL, PREG_SPLIT_OFFSET_CAPTURE);
    var_dump($matches);
    //foreach($diff as $diff_entry){
        $diff_entry = $diff;
        
            $separator = "\r\n";
            $line = strtok($diff_entry, $separator);
            echo "\t\t","<h2>",$line,"</h2>","\n";
            echo "\t<div class='diff'>";
            while ($line !== false) {            
                
                if(substr( $line, 0, 3 ) === "+++" || substr( $line, 0, 3 ) === "---"){
                    echo "\t\t","<pre class='commit' class='file'>",$line,"</pre>","\n";
                }
                $line = strtok( $separator );
                if(substr( $line, 0, 2 ) === "@@"){
                    break;
                }
            }
            while ($line !== false) {
                $type = "";
                //if(substr( $line, 0, 2 ) === "@@") $type = "class='info'";
                //else if(substr( $line, 0, 1 ) === "-") $type = "class='delete'";
                //else if(substr( $line, 0, 1 ) === "+") $type = "class='insert'";

                echo "\t\t","<pre class='commit'",$type,">",$line,"</pre>","\n";
                $line = strtok( $separator );
            }
        echo "\t\t","<br>","\n"    ;
        echo "\t","</div>","\n";
    //}

    pg_free_result($result);
    pg_close($dbconn);
    ?>
    </div>
</body>