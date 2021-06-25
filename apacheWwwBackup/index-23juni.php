<html lang="da-dk">
<head>
    <title>Allans env. status</title>

    <style>

    </style>
</head>
<body>


<h3>Allans env. status</h3>

<?php

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "dpscrawler";

$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}


echo "<table cellspacing='1'>\n";
echo "<tr>";
echo "<th></th>";

$fromTimeStamp = "2021-06-18 00:00:00";

$sqlTime = "SELECT distinct(timestamp)
FROM dpscrawler
where timestamp between '" . $fromTimeStamp . "' and '2029-01-01 00:00:00'
order by timestamp desc";
$resultTime = $conn->query($sqlTime);

while ($row = $resultTime->fetch_assoc()) {
    $timestamp = substr($row["timestamp"],5,11);
    $needleHit = false;
    foreach(array("00:00", "12:00", "18:00") as $needle) {
        if (strpos($timestamp, $needle ) !== FALSE) {
            $needleHit = true;
            break;
        }
    }

    if (strpos($timestamp, "07:00" ) !== FALSE) {
        echo "<td style='writing-mode: vertical-rl'><div><small>" . $timestamp . "</small></div></td> ";
    } else if ($needleHit) {
        echo "<td colspan='8' style='writing-mode: vertical-rl'><div><small>" . $timestamp . "</small></div></td> ";
    } else {
        echo "<td></td> ";
    }

}
echo "</tr>\n";


// --


$envs = array("p0", "m0", "es1", "et1", "et2", "et3", "et4", "xdt1", "t15", "t14", "t13", "t12", "t11", "t10", "t9", "t8", "t7", "t6", "t5", "t4", "t3", "t2", "t1", "t0", "i1", "d1");
foreach ($envs as $env) {

    $sqlStatus = "SELECT status, timestamp, lastcheck
    FROM dpscrawler
    where env = '" . $env . "'
    and timestamp between '" . $fromTimeStamp . "' and '2029-01-01 00:00:00'
    order by timestamp desc";
    $resultStatus = $conn->query($sqlStatus);
    if ($resultStatus->num_rows > 0) {
        echo "<tr>";
        echo "<td><small>" . $env . "</small></td> ";
        while ($row = $resultStatus->fetch_assoc()) {
            if ($row["status"] == '1') {
                $color = "green";
            } else {
                $color = "red";
            }
            echo "<td style='background-color:" . $color . "' title='" . $row["timestamp"] . " - " . $row["lastcheck"] . "'></td> ";

        }
        echo "</tr>\n";
    }
}


echo "</table>\n";

$conn->close();


?>

alsk@nykredit.dk


</body>
</html>
