package com.example.admin.androidtemplete;

import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import org.jsoup.nodes.Element;

import hugo.weaving.DebugLog;


public class Ticket {

    /** 値段 **/
    private int price = 0;
    /** 枚数 **/
    private String num = null;
    /** コメント **/
    private String comment = null;
    /** タイトル **/
    private String title = null;
    /** ETC **/
    private String etc = null;
    /** 日時 **/
    private String date = null;
    /** 開催地 **/
    private String place = null;
    /** リンク **/
    private String link = null;
    /** アーティスト **/
    private String artist = null;

    @DebugLog
    public Ticket(@NonNull String artist,@NonNull Element element) {
        Preconditions.checkNotNull(element);
        Preconditions.checkNotNull(artist);

        TicketParser parser = new TicketParser(element);
        initTicketData(parser);
        this.artist = artist;
    }

    @DebugLog
    private void initTicketData(@NonNull TicketParser parser) {
        Preconditions.checkNotNull(parser);

        price = parser.getTicketPrice();
        num = parser.getTicketNum();
        comment = parser.getTicketComment();
        title = parser.getTicketTitle();
        etc = parser.getTicketEtc();
        date = parser.getTicketDate();
        place = parser.getTicketPlace();
        link = parser.getTicketLink();
    }

    @DebugLog
    public int getPrice() {
        return price;
    }

    @DebugLog
    public String getNum() {
        return num;
    }

    @DebugLog
    public String getComment() {
        return comment;
    }

    @DebugLog
    public String getTitle() {
        return title;
    }

    @DebugLog
    public String getEtc() {
        return etc;
    }

    @DebugLog
    public String getDate() {
        return date;
    }

    @DebugLog
    public String getPlace() {
        return place;
    }

    @DebugLog
    public String getLink() {
        return link;
    }

    @DebugLog
    public String getArtist() {
        return artist;
    }
}
