package Hibernate.Entity;

import javax.annotation.PropertyKey;
import javax.persistence.*;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Created by Arnold on 09.04.2018.
 */
@Entity
@Table(name = "offer")
public class Dom {
    private String img;
    private Integer id;
    private String originalId;
    private String cost;
    private String name;
    @Temporal(TemporalType.TIMESTAMP)
    private String date;

    public Dom(){

    }

    public Dom(String originalId, String name, String cost, String img) {
        this.onCreate();
        this.originalId = originalId;
        this.name = name;
        this.cost = cost;
        this.img = img;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Integer getId() {
        return id;
    }

    @PrePersist
    protected void onCreate() {
        Date d = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = format.format(d);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @PropertyKey
    public String getOriginalId() {
        return originalId;
    }

    public void setOriginalId(String originalId) {
        this.originalId = originalId;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}

