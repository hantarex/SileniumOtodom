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
    public static final int Otodom = 1;
    public static final int Olx = 2;
    private String img;
    private Integer id;
    private String originalId;
    private String cost;
    private String oldCost = null;
    private String name;
    private String url;
    private Integer type = Otodom;
    @Temporal(TemporalType.TIMESTAMP)
    private String date;

    public Dom(){

    }

    public Dom(String originalId, String name, String cost, String img, String url, int type) {
        this.onCreate();
        this.originalId = originalId;
        this.name = name;
        this.cost = cost;
        this.img = img;
        this.url = url;
        this.type = type;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOldCost() {
        return oldCost;
    }

    public void setOldCost(String oldCost) {
        this.oldCost = oldCost;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}

