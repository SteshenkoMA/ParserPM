# ParserPM

Данный программа позволяет выгружать данные об утилизации ресурсов серверов приложений из HP Performance Manager в формат CSV и строить по ним графики PNG
![default](https://cloud.githubusercontent.com/assets/13558216/24837369/6df91038-1d3b-11e7-93fc-4dfe05941db7.png)    

• В папке FinalVersion - собранная уже настроенная программа, готовая к запуску

Для работы необходимо:

1) В папке parserPMconfig настроить:    
а) Файл timePath.properties    
path=C:\\Users\\sbt-Steshenko-MA\\Documents - путь в который сохранять результаты     
start=2017,02,25,03,00,00 end=2017,02,25,05,00,00 - промежуток времени за который необходимо выгрузить данные    
б) Файл urls.properties    
CPU_TOTAL_UTIL=http:/hp_pm_adress:1111/OVPM/?GRAPHTEMPLATE=YOUR_GRAPHTEMPLATE&GRAPH=CPU_TOTAL_UTIL&GRAPHTYPE=csv&STARTTIME=start&STOPTIME=end&POINTSEVERY=5&CUSTOMER=login&PASSWORD=password     
"CPU_TOTAL_UTIL" - название, которое необходимо задать для графика + путь до графика в HP Performance Manager   

2) Настроить файл для запуска программы start.bat  (передать в качестве параметра путь до конфигурационной папки): java -jar "parserPM.jar" "C:\Users\sbt-Steshenko-MA\Documents\NetBeansProjects\parserPM\FinalVersion\parserPMconfig"  

________________________________
__English__

This program allows you to download data about resource utilization of application servers from the HP Performance Manager to CSV format and build on them graphics PNG    

• In the folder "FinalVersion" - assembled already configured program ready to launch   

You need:   

1) Configure in the folder 'parserPMconfig':   
a) timePath.properties path=C:\\Users\\sbt-Steshenko-MA\\Documents - it is the path in which the results will be saved    start=2017,02,25,03,00,00 end=2017,02,25,05,00,00 - period of time for which data should be extracted 
b) urls.properties CPU_TOTAL_UTIL=http:/hp_pm_adress:1111/OVPM/?GRAPHTEMPLATE=YOUR_GRAPHTEMPLATE&GRAPH=CPU_TOTAL_UTIL&GRAPHTYPE=csv&STARTTIME=start&STOPTIME=end&POINTSEVERY=5&CUSTOMER=login&PASSWORD=password "CPU_TOTAL_UTIL" - the name that you want to set for the graph + path to the graph in HP Performance Manager    

2) Set the file start.bat to run the program  (send as parameter the path of the configuration folder): java-jar "parserPM.jar"    "C:\Users\sbt-Steshenko-MA\Documents\NetBeansProjects\parserPM\FinalVersion\parserPMconfig"   
