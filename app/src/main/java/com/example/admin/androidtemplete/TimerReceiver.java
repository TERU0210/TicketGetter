package com.example.admin.androidtemplete;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.activeandroid.query.Select;
import com.apkfuns.logutils.LogUtils;
import com.google.common.base.Preconditions;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import hugo.weaving.DebugLog;

@EReceiver
public class TimerReceiver extends BroadcastReceiver{

//    @Pref
//    MainPreference_ pref;

    SharedPreferences pref = null;

    static Context context = null;

    @DebugLog
    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(intent);

        this.context = context;

        String[] artists = getRawData();

        for (String artist : artists) {
            String[] param = artist.split(":");

            String url = param[0];
            String name = param[1];

            initTicket(url);
            checkTickets(url,name);
        }

//        if(pref.flag().get()) {
           new TimerUtil(context.getApplicationContext()).setTimer(60 * 1000);
//        }
    }

    @DebugLog
    String[] getRawData() {
        return context.getString(R.string.artist).split(",");
    }

    /**
     * チケットステータスを初期化
     * @param artist
     */
    @DebugLog
    @Background
    void initTicket(@NonNull String artist) {
        Preconditions.checkNotNull(artist);

        List<TicketEntity> tickets = new Select().from(TicketEntity.class).where("artist = ?", artist).execute();
        for (TicketEntity ticket : tickets) {
            // ステータスを初期化
            ticket.state = "";
            ticket.save();
        }
    }

    @DebugLog
    @Background
    void checkTickets(String artist,String name) {

            Document document = getRawDocument(artist);
            if(document.getElementsByClass("module-list-ticket") == null || document.getElementsByClass("module-list-ticket").text().length() == 0)
                return;

            Elements elements = getRawElements(document);

            for (Element element : elements) {
                if (element.getElementsByClass("ticket-status") == null || element.getElementsByClass("ticket-status").text().length() == 0) {
                    boolean flag = false;

                    Ticket ticket = new Ticket(artist,element);

                    //** Filter **/
                    // price
                    if(ticket.getPrice() > 15000) continue;

                    // title
                    String excludeTitles = context.getString(R.string.excludeTitle);
                    String title = ticket.getTitle();
                    LogUtils.d("excludeTitles = " + excludeTitles);
                    LogUtils.d("title = " + title);
                    for(String t : excludeTitles.split(",")) {
                        if(title.indexOf(t) >= 0) {
                            flag = true;
                            break;
                        }
                    }

                    // place
                    if(ticket.getPlace().indexOf("東京") < 0 && ticket.getPlace().indexOf("神奈川") < 0 && ticket.getPlace().indexOf("千葉") < 0  && ticket.getPlace().indexOf("埼玉") < 0) continue;
                    // comment
                    if(ticket.getComment().indexOf("完全見切れ") > 0) continue;


                    if(flag == false) {
                        TicketEntity entity = checkSameTicket(ticket.getLink());
                        if (entity == null) {
                            // 新規チケット追加
                            createTicket(ticket);
                        } else {
                            // 情報を更新
                            updateTicket(entity);
                        }
                    }
                }
            }

        // 新規追加チケットをメールする
        initMail(artist, name);
        // 削除されたチケットをDBから削除する
        removeTicket(artist);
    }

    @DebugLog
    Document getRawDocument(@NonNull String url) {
        Preconditions.checkNotNull(url);
        Document retVal = null;

        try {
            retVal =  Jsoup.connect("http://ticketcamp.net/" + url +"-tickets/").get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Preconditions.checkNotNull(retVal);
        return retVal;
    }

    @DebugLog
    Elements getRawElements(@NonNull Document document) {
        Preconditions.checkNotNull(document);

        Elements retVal = document.getElementsByClass("module-list-ticket").first().getElementsByClass("row");

        Preconditions.checkNotNull(retVal);
        return  retVal;
    }



    @DebugLog
    TicketEntity checkSameTicket(String link) {
        Preconditions.checkNotNull(link);

        // 同一のチケットがあるか確認
        TicketEntity retVal = new Select().from(TicketEntity.class).where("link = ?", link).executeSingle();

        return retVal;
    }

    @DebugLog
    @Background
    void initMail(@NonNull String artist, @NonNull String name) {
        Preconditions.checkNotNull(artist);
        Preconditions.checkNotNull(name);

        List<TicketEntity> tickets = new Select().from(TicketEntity.class).where("artist = ? and state = ? ", artist,"NEW").execute();
        if(tickets.size() > 0) {
            sendMail(name,tickets);
        }
    }

    @DebugLog
    @Background
    void removeTicket(String artist) {
        List<TicketEntity> tickets = new Select().from(TicketEntity.class).where("artist = ?", artist).execute();

        if(tickets.size() <= 0) {
            return;
        }

        // ステータスコードがついてないものは削除する
        for (TicketEntity ticket : tickets) {
            if(ticket.state.length() == 0) {
                ticket.delete();
            }
        }
    }


    /**
     * チケット情報を元にメールの本文を作成する
     * @param tickets Ticket
     */
    @DebugLog
    @Background
    void sendMail(String artist,List<TicketEntity> tickets) {
        Preconditions.checkNotNull(artist);
        Preconditions.checkNotNull(tickets);

        StringBuilder builder = new StringBuilder();
        builder.append("\n" + artist + "\n\n");
        for (TicketEntity ticket : tickets) {
            builder.append("公演名 : " + ticket.title +"\n");
            builder.append("公演日時 : " + ticket.date +"\n");
            builder.append("会場 : " + ticket.place +"\n");
            builder.append("詳細 : " + ticket.comment +"\n");
            builder.append("枚数 : " + ticket.num +"\n");
            builder.append("価格 : " + ticket.price +"\n");
            builder.append("バラ売り : " + ticket.etc +"\n");
            builder.append("URL : " + ticket.link +"\n");

            builder.append("\n\n");
        }

        if(builder.toString().length() > 0) {
            sendMail(builder.toString());
        }
    }

    /**
     * メールを
     * @param message
     */
    @DebugLog
    @Background
    void sendMail(String  message) {
        final String email = "";
        final String password = "";
        String body = message;
        String subject = "TicketCamp";

        try {

            //以下メール送信
            final Properties property = new Properties();
            property.put("mail.smtp.host",                "smtp.gmail.com");
            property.put("mail.host",                     "smtp.gmail.com");
            property.put("mail.smtp.port",                "465");
            property.put("mail.smtp.socketFactory.port",  "465");
            property.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

            // セッション
            final Session session = Session.getInstance(property, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(email, password);
                }
            });

            MimeMessage mimeMsg = new MimeMessage(session);

            mimeMsg.setSubject(subject, "utf-8");
            mimeMsg.setFrom(new InternetAddress(email));
            mimeMsg.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(email));

            final MimeBodyPart txtPart = new MimeBodyPart();
            txtPart.setText(body, "utf-8");


            final Multipart mp = new MimeMultipart();
            mp.addBodyPart(txtPart);
            mimeMsg.setContent(mp);

            // メール送信する。
            final Transport transport = session.getTransport("smtp");
            transport.connect(email,password);
            transport.sendMessage(mimeMsg, mimeMsg.getAllRecipients());
            transport.close();

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    @DebugLog
    @Background
    void createTicket(@NonNull Ticket ticket) {
        TicketEntity entity = new TicketEntity();

        entity.state = "NEW";
        entity.artist = ticket.getArtist();
        entity.title = ticket.getTitle();
        entity.date = ticket.getDate();
        entity.place = ticket.getPlace();
        entity.comment = ticket.getComment();
        entity.num = ticket.getNum();
        entity.price = ticket.getPrice();
        entity.etc = ticket.getEtc();
        entity.link = ticket.getLink();

        entity.save();
    }

    @DebugLog
    @Background
    void updateTicket(TicketEntity entity) {
        entity.state = "OLD";
        entity.save();
    }


}