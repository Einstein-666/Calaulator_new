package com.example.calaulator_new;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {
    public static String conversion(String s) {
        char op = ' ';
        int ina,ins,inm,ind,index = -1;
        ina = s.indexOf('+');
        ins = s.indexOf('-');
        inm = s.indexOf('*');
        ind = s.indexOf('/');
        if(ina != -1) {
            op = '+';
            index = ina;
        }
        if(ins != -1) {
            op = '-';
            index = ins;
        }
        if(inm != -1) {
            op = '*';
            index = inm;
        }
        if(ind != -1) {
            op = '/';
            index = ind;
        }
        String re = "";
        Double num1 = Double.valueOf(s.substring(0,index));
        Double num2 = Double.valueOf(s.substring(index+1));
        Double result;
        switch (op){
            case '+':
                result = num1 + num2;
                break;
            case '-':
                result = num1 - num2;
                break;
            case '*':
                result = num1 * num2;
                break;
            case ' ':
                result = 0.;
                break;
            default:
                if(num2 == 0)
                    throw new IllegalStateException("除数不能为0");
                else
                    result = num1 / num2;
        }
        re = String.valueOf(result);
        return re;
    }
}
