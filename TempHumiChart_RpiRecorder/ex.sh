#!/bin/bash
#source $HOME/.bash_profile
python /path/to/TempHumiChart.py 18 23 -g 'BOARD' | tee -a /path/to/data.txt /path/to/dataDashBoard.txt

dt=$(awk -F';' 'END { print $1 }' /home/pi/Adafruit_Python_DHT/data/dataDashBoard.txt);
t=$(awk -F';' 'END { print $2 }' /home/pi/Adafruit_Python_DHT/data/dataDashBoard.txt);
h=$(awk -F';' 'END { print $3 }' /home/pi/Adafruit_Python_DHT/data/dataDashBoard.txt);

url="https://catprogrammer.com/DashBoard/uploadData.php?dt=";
url=${url}${dt};
url=${url}"&t=";
url=${url}${t};
url=${url}"&h=";
url=${url}${h};

url=$(echo $url | sed -e "s/ /+/");

curl $url;
