<!DOCTYPE html>

<html>
    <?php
        include 'header.html';
    ?>

<p>
<form action=searchform.php method=GET>
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
    <td>Commit after:</td>
    <td><input type="date" name="date" size=15 maxlength=15></td>
  </tr>
  <tr>
    <td></td>
    <td>Limit Output to</td>
    <td><input type="number" name="limit" value="50" size=15 maxlength=15 ></td>
  </tr>
  <tr>
    <td><input type="checkbox" name="vehicle2" value="message_search"></td>
    <td>Commit message contains:</td>
    <td><input type=text name=contains size=15 maxlength=15></td>
  </tr>
</table> 
<p>
<input type=submit>
</center>

</form>
</body>
</html>


