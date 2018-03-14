package myset;

public class ELectricUseData
{
    public String _id;
    public String name;
    public String timeStart;
    public String timeOver;
    public String timeUse;
    public String electricQuantity;

    public ELectricUseData()
    {
    }

    public ELectricUseData(String name, String timeStart, String timeOver, String electricQuantity, String timeUse)
    {
        this.name = name;
        this.timeStart = timeStart;
        this.timeOver = timeOver;
        this.timeUse=timeUse;
        this.electricQuantity=electricQuantity;
    }

}