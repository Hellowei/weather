package com.example.vincent.model;

/**
 * Created by Vincent on 2016/3/25.
 */
public class Weather {
    private int id;
    private int countryCode;//某地的代码
    private String countryName;
    private int weatherDate;//具体哪天的天气
    private int updateTime;//这些数据最后更新的时间
    private String fa;//白天天气
    private String fb;//晚上天气编号
    private int fc;//白天温度
    private int fd;//晚上温度
    private int fe;//白天风向
    private int ff;//晚上风向
    private int fg;//白天风力
    private int fh;//晚上风力
    private String fi;//日出日落时间
    public void setId(int id){ this.id = id;}
    public void setCountryCode(int a){ this.countryCode = a;}
    public void setCountryName(String a){ this.countryName = a;}
    public void setWeatherDate (int a){ this.weatherDate  = a;}
    public void setUpdateTime (int a){ this.updateTime  = a;}
    public void setFa (String a){ this.fa  = a;}
    public void setFb (String a){ this.fb  = a;}
    public void setFc (int a){ this.fc  = a;}
    public void setFd (int a){ this.fd  = a;}
    public void setFe (int a){ this.fe  = a;}
    public void setFf (int a){ this.ff  = a;}
    public void setFg (int a){ this.fg  = a;}
    public void setFh (int a){ this.fh  = a;}
    public void setFi (String a){ this.fi  = a;}

    public int getId(){ return id;}
    public int getCountryCode(){    return countryCode;}
    public String getCountryName(){    return  countryName;}
    public int getWeatherDate(){    return  weatherDate;}
    public int getUpdateTime(){ return updateTime;}
    public String getFa(){  return fa;}
    public String getFb(){  return fb;}
    public int getFc(){ return fc;}
    public int getFd(){ return fd;}
    public int getFe(){ return fe;}
    public int getFf(){ return ff;}
    public int getFg(){ return fg;}
    public int getFh(){ return fh;}
    public String getFi(){return fi;}

}
