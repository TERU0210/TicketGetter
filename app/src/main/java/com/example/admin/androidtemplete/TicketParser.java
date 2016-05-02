package com.example.admin.androidtemplete;

import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;

import hugo.weaving.DebugLog;

/**
 * Created by Admin on 2016/03/13.
 */
public class TicketParser {

    private Element element = null;

    @DebugLog
    public TicketParser(@NonNull Element element) {
        Preconditions.checkNotNull(element);

        this.element = element;
    }

    @DebugLog
    int getTicketPrice() {
        Preconditions.checkNotNull(element);
        int retVal = 0;

        String price = element.getElementsByClass("ticket-price").text();
        retVal = Integer.parseInt(StringUtils.remove(price, ","));

        Preconditions.checkArgument(retVal >= 0);
        return retVal;
    }

    @DebugLog
    String getTicketTitle() {
        Preconditions.checkNotNull(element);

        String retVal = element.getElementsByClass("h").text();

        Preconditions.checkNotNull(retVal);
        return retVal;
    }

    @DebugLog
    String getTicketComment() {
        Preconditions.checkNotNull(element);

        String retVal = element.getElementsByClass("description").text();

        Preconditions.checkNotNull(retVal);
        Preconditions.checkArgument(retVal.length() != 0);
        return retVal;
    }

    @DebugLog
    String getTicketEtc() {
        Preconditions.checkNotNull(element);

        String retVal = element.select("small").text();

        Preconditions.checkNotNull(retVal);
        return retVal;
    }

    @DebugLog
    String getTicketNum() {
        Preconditions.checkNotNull(element);

        String retVal = element.getElementsByClass("price").select("p").select("span").text();

        Preconditions.checkNotNull(retVal);
        Preconditions.checkArgument(retVal.length() != 0);
        return  retVal;
    }

    @DebugLog
    String getTicketDate() {
        Preconditions.checkNotNull(element);

        String retVal = element.getElementsByClass("date").text();


        Preconditions.checkNotNull(retVal);
        Preconditions.checkArgument(retVal.length() != 0);
        return retVal;
    }

    @DebugLog
    String getTicketPlace() {
        Preconditions.checkNotNull(element);

        String retVal = element.getElementsByClass("place").text();

        Preconditions.checkNotNull(element);
        Preconditions.checkArgument(retVal.length() != 0);
        return retVal;
    }

    @DebugLog
    String getTicketLink() {
        Preconditions.checkNotNull(element);

        String retVal  = element.getElementsByClass("h").select("a").attr("href");

        Preconditions.checkNotNull(retVal);
        Preconditions.checkArgument(retVal.length() != 0);
        return retVal;
    }
}
