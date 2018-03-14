package myset;

public class TimingData
{
    public String OnOff;
    public String time;
    public String repetition;
    public String ifWork;

    public TimingData()
    {
    }

    public TimingData(String OnOff, String time, String repetition, String ifWork)
    {
    	this.OnOff = OnOff;
        this.time = time;
        this.repetition = repetition;
        this.ifWork=ifWork;
    }
}