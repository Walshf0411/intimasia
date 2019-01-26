package com.peppermintcommunications.intimasiaregistration;

import android.text.Html;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowAgendaExpandableListDataPump {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> day1 = new ArrayList<String>();
        day1.add(Html.fromHtml("<b>10:30 AM</b><br> Inaugration Ceremony<br/>" +
                "<b>Chief Guest:</b><br/> Smt Smriti Irani, Hon'ble Textile Minister<br/>" +
                "<br><b>Guest of Honour:</b><br/>" +
                "Shri Ajay Tamta, Hon’ble Minister of State for Textiles<br/>" +
                "Shri Shahnawaz Husain, National Spokesperson, BJP<br/>" +
                "Shri Rakesh Grover, President, Intimate Apparel Association of India<br/>" +
                "Shri Deepak Kumar, Executive Director, ITPO").toString());
        day1.add(Html.fromHtml("<b>11:30 AM<b/> Thematic Performance on Lingerie").toString());
        Log.i("asdfghjkl", Html.fromHtml("<b>11:30 AM<b/><br> Thematic Performance on Lingerie").toString());
        day1.add(Html.fromHtml("<b>12:30 PM</b><br> <b>Presentation on “Creating WoW Experience for Intimate Wear Shopping<b/><br/>" +
                "<b>Presented By:</b> Ms. Vimmi Sood").toString());
        day1.add(Html.fromHtml("<b>03.00 PM<b/> <br>Thematic Performance on Active Wear").toString());
        day1.add(Html.fromHtml("<b>04:00 PM<b/><br> Thematic Performance on Yoga Wear").toString());
        day1.add(Html.fromHtml("<b>04:00 PM<b/><br> Lucky Draw for Retailers").toString());
        day1.add(Html.fromHtml("<b>07.00 PM<b/><br> Fair Concludes for the Day").toString());
        day1.add(Html.fromHtml("<b>08:00 PM<b/><br> Inner Secrets Excellence Awards (Retail for Delhi & NCR Region)").toString());

        List<String> day2 = new ArrayList<String>();
        day2.add(Html.fromHtml("<b>11:00 AM<b/><br> Thematic Performance on Lingere").toString());
        day2.add(Html.fromHtml("<b>12:00 PM<b/><br> <b>Presentation on Increasing Shopping Experience for Intimate Wears<b/><br/>" +
                "<b>Presented By:</b> Ms. Vimmi Sood<br/>").toString());
        day2.add(Html.fromHtml("<b>03:00 PM<b/><br> Thematic Performance on Active Wear").toString());
        day2.add(Html.fromHtml("<b>04.00 PM<b/><br> Thematic Performance on Yoga Wear").toString());
        day2.add(Html.fromHtml("<b>05.30 PM<b/><br> Lucky Draw for Retailers").toString());
        day2.add(Html.fromHtml("<b>07.00 PM<b/><br> Fair Concludes for the Day").toString());

        expandableListDetail.put("Day 01: 21 January 2019", day1);
        expandableListDetail.put("Day 02: 22 January 2019", day2);

        return expandableListDetail;
    }
}
