package com.oushu.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hello {


    public static void main(String[] args) {
        String d = ">= 2 && <= 4";
        Pattern p = Pattern.compile("^>= | <= | > | <");
        Matcher m = p.matcher(d);
        while (m.find()){
            String group = m.group().trim();
            System.out.println(group);
        }


    }
}
