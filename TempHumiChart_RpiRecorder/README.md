   # TempHumiChart_RpiRecorder

   Sensor: rsh1x

   Require pi-sht1x

   pip# install pi-sht1x
   
   Use TempHumiChart.py to read temperature and humidity data.

   Run ex.sh to run TempHumiChart.py and save data to local and upload to server.

   I use crontab to run ex.sh with root every 10 minuits.
