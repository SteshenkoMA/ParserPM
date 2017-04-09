/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parserpm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 *
 * @author sbt-steshenko-ma
 */

//Класс, который работает датой из файла timePath.properties
public class TimeParser {
    
    private Date date;
    private String text;
    
    TimeParser(String text) {
        this.text = text;
        setDateTime();
    }
    
    public Date getDate(){
        return date;
    }
        
    public void setDateTime(){
       
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy,MM,dd,HH,mm,ss");
       
       try{
         date = sdf.parse(text);

       }
       catch(ParseException e ){
       }



        }
     
        
    }
    
    

