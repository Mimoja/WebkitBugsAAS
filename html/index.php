
<?php
    include 'header.php';
?>

<p>
<form action=search.php method=GET>
Search for:
<center>
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
</body>
</html>


