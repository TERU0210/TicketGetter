package com.example.admin.androidtemplete;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import hugo.weaving.DebugLog;

@Table(name = "Tickets")
public class TicketEntity extends Model {

    @Column(name = "State")
    public String state;
    @Column(name = "Artist")
    public String artist;
    @Column(name = "Title")
    public String title;
    @Column(name = "Date")
    public String date;
    @Column(name = "Place")
    public String place;
    @Column(name = "Comment")
    public String comment;
    @Column(name = "Num")
    public String num;
    @Column(name = "Price")
    public int price;
    @Column(name = "Etc")
    public String etc;
    @Column(name = "Link", unique = true,onUniqueConflict = Column.ConflictAction.FAIL)
    public String link;

    @DebugLog
    public TicketEntity() {
        super();
    }

}