
<?php
include 'header.php';
// Connecting, selecting database
$dbconn = pg_connect("host=localhost dbname=postgres user=postgres password=dummy")
    or die('Could not connect: ' . pg_last_error());

// Performing SQL query
$query = 'SELECT * FROM commits LIMIT 50';
$result = pg_query($query) or die('Query failed: ' . pg_last_error());

// Printing results in HTML
echo "<table>\n";
while ($line = pg_fetch_array($result, null, PGSQL_ASSOC)) {
    echo "\t<tr>\n";
    
    echo "\t\t<td>".base64_decode($line["message"])."</td>\n";
    
    //var_dump($line);
    echo "\t</tr>\n";
}
echo "</table>\n";

// Free resultset
pg_free_result($result);

// Closing connection
pg_close($dbconn);

?>
