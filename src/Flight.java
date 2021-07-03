

import java.util.Date;

public class Flight {
    private String flightId;
    private String dept;
    private String arv;
    private Date departureDate;
    private String duration;
    private String price;


    public Flight(String flightId, String dept, String arv, Date departureDate, String duration, String price) {
        this.flightId = flightId;
        this.dept = dept;
        this.arv = arv;
        this.departureDate = departureDate;
        this.duration = duration;
        this.price = price;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getArv() {
        return arv;
    }

    public void setArv(String arv) {
        this.arv = arv;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
