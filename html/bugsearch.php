
<html>
<body>

<?php

mysql_connect (<MySQLserver>, <username>, <password>);

mysql_select_db (<dbname>);

if ($fname == "")
{$fname = '%';}

if ($lname == "")
{$lname = '%';}

$result = mysql_query ("SELECT * FROM <tablename>	
	WHERE fname LIKE '$fname%'
	AND lname LIKE '$lname%'
	");

if ($row = mysql_fetch_array($result)) {

do {
	print $row["fname"];
	print (" ");
	print $row["lname"];
	print("<p>");
} while($row = mysql_fetch_array($result));
} else {print "Sorry, no records were found!";
}

?>

</body>
</html>

# ---- End searchform.php



<?php
/*
// Connecting, selecting database
$dbconn = pg_connect("host=localhost dbname=publishing user=www password=foo")
    or die('Could not connect: ' . pg_last_error());

// Performing SQL query
$query = 'SELECT * FROM authors';
$result = pg_query($query) or die('Query failed: ' . pg_last_error());

// Printing results in HTML
echo "<table>\n";
while ($line = pg_fetch_array($result, null, PGSQL_ASSOC)) {
    echo "\t<tr>\n";
    foreach ($line as $col_value) {
        echo "\t\t<td>$col_value</td>\n";
    }
    echo "\t</tr>\n";
}
echo "</table>\n";

// Free resultset
pg_free_result($result);

// Closing connection
pg_close($dbconn);
*/
?>
