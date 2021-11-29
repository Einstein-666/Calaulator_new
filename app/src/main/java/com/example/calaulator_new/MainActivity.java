package com.example.calaulator_new;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.math.BigDecimal;
import java.util.StringTokenizer;

public class MainActivity extends Activity {

    /**
     * 定义变量
     */
    //舍入精度
    public  static final  int[] DEF_DIV_SCALE = {17};
    //初始化显示数据
    private final String[] init =new String[1];
    //显示框
    private EditText input;// 用于显示输出结果
    //划动条
    private SeekBar seekBar;
    //普通控件及变量
    private Button[] btn = new Button[10];// 0~9十个数字
    private TextView mem, _drg, tip;
    private Button div, mul, sub, add, equal, sin, cos, tan, log, ln, sqrt,
            square, factorial, bksp, left, right, dot, bd, db, drg, mc, c;
    public String str_old;
    public String str_new;
    public boolean vbegin = true;// 控制输入，true为重新输入，false为接着输入
    public boolean drg_flag = true;// true为角度，false为弧度
    public double pi = 4 * Math.atan(1);// π值
    public boolean tip_lock = true;// true为正确，可以继续输入，false错误，输入锁定
    public boolean equals_flag = true;// 是否在按下=之后输入，true为之前，false为之后

    //定义ContextMenu中每个菜单选项的Id
    final int Menu_1 = Menu.FIRST;
    final int Menu_2 = Menu.FIRST + 1;
    private ClipboardManager mClipboard = null;


    /*
     *onCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //切换布局后调用，初始化显示
        if(savedInstanceState!=null){
            init[0] = savedInstanceState.getString("exp");
            equals_flag = false;
        }

        //屏幕方向监听
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 横屏内容
            setContentView(R.layout.activity_main2);
            InitWigdet();
            AllWigdetListener();
        }

        else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 竖屏内容
            setContentView(R.layout.activity_main);
            input = (EditText)findViewById(R.id.textView);
            input.setText(init[0]);
            seekBar = (SeekBar) findViewById(R.id.seekBar);
            seekBar.setProgress(DEF_DIV_SCALE[0]);
            seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
            Display defaultDisplay = getWindowManager().getDefaultDisplay();
            Point point = new Point();
            defaultDisplay.getSize(point);
            Integer x = point.x;
            Integer y = point.y;

            input.setKeyListener(new KeyListener() {
                @Override
                public int getInputType() {
                    return 0;
                }

                @Override
                public boolean onKeyDown(View view, Editable editable, int i, KeyEvent keyEvent) {
                    return false;
                }

                @Override
                public boolean onKeyUp(View view, Editable editable, int i, KeyEvent keyEvent) {
                    return false;
                }

                @Override
                public boolean onKeyOther(View view, Editable editable, KeyEvent keyEvent) {
                    return false;
                }

                @Override
                public void clearMetaKeyState(View view, Editable editable, int i) {

                }
            });
            View.OnClickListener onClearListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    input.setText("");
                }
            };
            View.OnClickListener onClearLastListener = new View.OnClickListener() {
                //删除按键
                @Override
                public void onClick(View view) {
                    String s = input.getText().toString();
                    int len = s.length();
                    if(len>0) {
                        s = s.substring(0, len - 1);
                        input.setText(s);
                    }
                }
            };

            View.OnClickListener onEXPListener = new View.OnClickListener() {
                // 操作符
                @Override
                public void onClick(View view) {
                    Button temp = (Button)view;
                    String s = temp.getText().toString();
                    String show = input.getText().toString();
                    int len = show.length();

                    if(show.length()==0){
                        if(s.equals("+")||s.equals("×")||s.equals("÷")) return;
                    }
                    if((show.endsWith("+"))||(show.endsWith("-"))||(show.endsWith("×"))||(show.endsWith("÷"))||(show.endsWith("."))){

                        if(s.equals("+")||s.equals("×")||s.equals("÷")||s.equals("-")||(s.equals("."))){
                            show = show.substring(0,len-1) + s;
                            input.setText(show);
                        }else
                            input.append(s);
                    }else{
                        input.append(s);
                    }
                }
            };

            View.OnClickListener onEXECListener = new View.OnClickListener() {
                // =
                @Override
                public void onClick(View view) {
                    String s = input.getText().toString();
                    if(s.length()==0) return;
                    s = s.replace("×","*");
                    s = s.replace("÷","/");
                    s = s.replace("%","");
                    if((s.endsWith("+"))||(s.endsWith("-"))||(s.endsWith("*"))||(s.endsWith("/"))||(s.endsWith(".")))return;

                    try{
                        String re = Calculator.conversion(s);
                        re = round(re,DEF_DIV_SCALE[0]);
                        if(re.indexOf(".") > 0){
                            re = re.replaceAll("0+?$", "");//去掉多余的0
                            re = re.replaceAll("[.]$", "");//如最后一位是.则去掉
                        }
                        input.setText(re);
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        input.setText("");
                    }

                }
            };
            View.OnClickListener onPERCENTCListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String s = input.getText().toString();
                    if(s.length()==0) return;
                    s = s.replace("×","*");
                    s = s.replace("÷","/");
                    s = s.replace("%","");
                    if((s.endsWith("+"))||(s.endsWith("-"))||(s.endsWith("*"))||(s.endsWith("/"))||(s.endsWith("."))) return;
                    s = s+"/100";

                    try{
                        String re = Calculator.conversion(s);
                        re = round(re,DEF_DIV_SCALE[0]);
                        if(re.indexOf(".") > 0){
                            re = re.replaceAll("0+?$", "");//去掉多余的0
                            re = re.replaceAll("[.]$", "");//如最后一位是.则去掉
                        }
                        input.setText(re+"%");
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        input.setText("");
                    }

                }
            };
            input.getLayoutParams().height = 2*y/7;
            Button button5 = (Button)findViewById(R.id.button5);    button5.getLayoutParams().width = x/4 ;button5.getLayoutParams().height = y/8;
            Button button6 = (Button)findViewById(R.id.button6);    button6.getLayoutParams().width = x/4 ;button6.getLayoutParams().height = y/8;
            Button button7 = (Button)findViewById(R.id.button7);    button7.getLayoutParams().width = x/4 ;button7.getLayoutParams().height = y/8;
            Button button8 = (Button)findViewById(R.id.button8);    button8.getLayoutParams().width = x/4 ;button8.getLayoutParams().height = y/8;
            Button button9 = (Button)findViewById(R.id.button9);    button9.getLayoutParams().width = x/4 ;button9.getLayoutParams().height = y/8;
            Button button10 = (Button)findViewById(R.id.button10);  button10.getLayoutParams().width = x/4 ;button10.getLayoutParams().height = y/8;
            Button button11 = (Button)findViewById(R.id.button11);  button11.getLayoutParams().width = x/4 ;button11.getLayoutParams().height = y/8;
            Button button12 = (Button)findViewById(R.id.button12);  button12.getLayoutParams().width = x/4 ;button12.getLayoutParams().height = y/8;
            Button button13 = (Button)findViewById(R.id.button13);  button13.getLayoutParams().width = x/4 ;button13.getLayoutParams().height = y/8;
            Button button14 = (Button)findViewById(R.id.button14);  button14.getLayoutParams().width = x/4 ;button14.getLayoutParams().height = y/8;
            Button button15 = (Button)findViewById(R.id.button15);  button15.getLayoutParams().width = x/4 ;button15.getLayoutParams().height = y/8;
            Button button16 = (Button)findViewById(R.id.button16);  button16.getLayoutParams().width = x/4 ;button16.getLayoutParams().height = y/8;
            Button button17 = (Button)findViewById(R.id.button17);  button17.getLayoutParams().width = x/4 ;button17.getLayoutParams().height = y/8;
            Button button18 = (Button)findViewById(R.id.button18);  button18.getLayoutParams().width = x/4 ;button18.getLayoutParams().height = y/8;
            Button button19 = (Button)findViewById(R.id.button19);  button19.getLayoutParams().width = x/4 ;button19.getLayoutParams().height = y/8;
            Button button20 = (Button)findViewById(R.id.button20);  button20.getLayoutParams().width = x/4 ;button20.getLayoutParams().height = 2*(y/8);
            Button button21 = (Button)findViewById(R.id.button21);  button21.getLayoutParams().width = x/4 ;button21.getLayoutParams().height = y/8;
            Button button22 = (Button)findViewById(R.id.button22);  button22.getLayoutParams().width = x/4 ;button22.getLayoutParams().height = y/8;
            Button button23 = (Button)findViewById(R.id.button23);  button23.getLayoutParams().width = x/4 ;button23.getLayoutParams().height = y/8;

            button6.setOnClickListener(onEXPListener);button7.setOnClickListener(onEXPListener);
            button9.setOnClickListener(onEXPListener);button10.setOnClickListener(onEXPListener);
            button11.setOnClickListener(onEXPListener);button12.setOnClickListener(onEXPListener);
            button13.setOnClickListener(onEXPListener);button14.setOnClickListener(onEXPListener);
            button15.setOnClickListener(onEXPListener);button16.setOnClickListener(onEXPListener);
            button17.setOnClickListener(onEXPListener);button18.setOnClickListener(onEXPListener);
            button19.setOnClickListener(onEXPListener);button21.setOnClickListener(onEXPListener);
            button22.setOnClickListener(onEXPListener);button23.setOnClickListener(onEXPListener);
            button5.setOnClickListener(onClearListener);
            button8.setOnClickListener(onClearLastListener);
            button20.setOnClickListener(onEXECListener);
            button21.setOnClickListener(onPERCENTCListener);
        }
    }

    /**
     * 初始化所有的组件
     */
    private void InitWigdet() {
        //获取屏幕参数
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        Integer x = point.x;
        Integer y = point.y - 60;
        // 获取界面元素
        input = (EditText) findViewById(R.id.input);
        input.setKeyListener(new KeyListener() {
            @Override
            public int getInputType() {
                return 0;
            }

            @Override
            public boolean onKeyDown(View view, Editable editable, int i, KeyEvent keyEvent) {
                return false;
            }

            @Override
            public boolean onKeyUp(View view, Editable editable, int i, KeyEvent keyEvent) {
                return false;
            }

            @Override
            public boolean onKeyOther(View view, Editable editable, KeyEvent keyEvent) {
                return false;
            }

            @Override
            public void clearMetaKeyState(View view, Editable editable, int i) {

            }
        });
        input.setText(init[0]);

        seekBar = (SeekBar) findViewById(R.id.seekBar); //seekBar.getLayoutParams().height = y/7/3;
        seekBar.setProgress(DEF_DIV_SCALE[0]);
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

        input.getLayoutParams().height = y/9;
        mem = (TextView) findViewById(R.id.mem);
        tip = (TextView) findViewById(R.id.tip);
        _drg = (TextView) findViewById(R.id._drg); _drg.getLayoutParams().width = x/6 ;_drg.getLayoutParams().height = y/10-8;
        btn[0] = (Button) findViewById(R.id.zero); btn[0].getLayoutParams().width = x/6 ;btn[0].getLayoutParams().height = y/10;
        btn[1] = (Button) findViewById(R.id.one); btn[1].getLayoutParams().width = x/6 ;btn[1].getLayoutParams().height = y/10;
        btn[2] = (Button) findViewById(R.id.two); btn[2].getLayoutParams().width = x/6 ;btn[2].getLayoutParams().height = y/10;
        btn[3] = (Button) findViewById(R.id.three); btn[3].getLayoutParams().width = x/6 ;btn[3].getLayoutParams().height = y/10;
        btn[4] = (Button) findViewById(R.id.four); btn[4].getLayoutParams().width = x/6 ;btn[4].getLayoutParams().height = y/10;
        btn[5] = (Button) findViewById(R.id.five); btn[5].getLayoutParams().width = x/6 ;btn[5].getLayoutParams().height = y/10;
        btn[6] = (Button) findViewById(R.id.six); btn[6].getLayoutParams().width = x/6 ;btn[6].getLayoutParams().height = y/10;
        btn[7] = (Button) findViewById(R.id.seven); btn[7].getLayoutParams().width = x/6 ;btn[7].getLayoutParams().height = y/10;
        btn[8] = (Button) findViewById(R.id.eight); btn[8].getLayoutParams().width = x/6 ;btn[8].getLayoutParams().height = y/10;
        btn[9] = (Button) findViewById(R.id.nine); btn[9].getLayoutParams().width = x/6 ;btn[9].getLayoutParams().height = y/10;
        div = (Button) findViewById(R.id.divide); div.getLayoutParams().width = x/6 ;div.getLayoutParams().height = y/10;
        mul = (Button) findViewById(R.id.mul);mul.getLayoutParams().width = x/6 ;mul.getLayoutParams().height = y/10;
        sub = (Button) findViewById(R.id.sub); sub.getLayoutParams().width = x/6 ;sub.getLayoutParams().height = y/10;
        add = (Button) findViewById(R.id.add); add.getLayoutParams().width = x/6 ;add.getLayoutParams().height = y/10;
        equal = (Button) findViewById(R.id.equal); equal.getLayoutParams().width = 2*x/6 ;equal.getLayoutParams().height = y/10;
        sin = (Button) findViewById(R.id.sin); sin.getLayoutParams().width = x/6 ;sin.getLayoutParams().height = y/10-8;
        cos = (Button) findViewById(R.id.cos); cos.getLayoutParams().width = x/6 ;cos.getLayoutParams().height = y/10-8;
        tan = (Button) findViewById(R.id.tan); tan.getLayoutParams().width = x/6 ;tan.getLayoutParams().height = y/10-8;
        log = (Button) findViewById(R.id.log); log.getLayoutParams().width = x/6 ;log.getLayoutParams().height = y/10;
        ln = (Button) findViewById(R.id.ln); ln.getLayoutParams().width = x/6 ;ln.getLayoutParams().height = y/10;
        sqrt = (Button) findViewById(R.id.sqrt); sqrt.getLayoutParams().width = x/6 ;sqrt.getLayoutParams().height = y/10;
        square = (Button) findViewById(R.id.square); square.getLayoutParams().width = x/6 ;square.getLayoutParams().height = y/10;
        factorial = (Button) findViewById(R.id.factorial);factorial.getLayoutParams().width = x/6 ;factorial.getLayoutParams().height = y/10-8;
        bksp = (Button) findViewById(R.id.bksp);bksp.getLayoutParams().width = 2*x/6 ;bksp.getLayoutParams().height = y/10-8;
        left = (Button) findViewById(R.id.left); left.getLayoutParams().width = x/6 ;left.getLayoutParams().height = y/10;
        right = (Button) findViewById(R.id.right); right.getLayoutParams().width = x/6 ;right.getLayoutParams().height = y/10;
        dot = (Button) findViewById(R.id.dot);dot.getLayoutParams().width = x/6 ;dot.getLayoutParams().height = y/10;
        bd = (Button) findViewById(R.id.bd);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear);linearLayout.getLayoutParams().width = x/6 ;linearLayout.getLayoutParams().height = y/10;
        db = (Button) findViewById(R.id.db);

        drg = (Button) findViewById(R.id.drg);drg.getLayoutParams().width = x/6 ;drg.getLayoutParams().height = y/10-8;
        mc = (Button) findViewById(R.id.mc); mc.getLayoutParams().width = 2*x/6 ;mc.getLayoutParams().height = y/10-8;
        c = (Button) findViewById(R.id.c); c.getLayoutParams().width = 2*x/6 ;c.getLayoutParams().height = y/10-8;
    }

    /**
     * 为所有按键绑定监听器
     */
    private void AllWigdetListener() {
        // 数字键
        for (int i = 0; i < 10; i++) {
            btn[i].setOnClickListener(actionPerformed);
        }
        // 为+-x÷等按键绑定监听器
        div.setOnClickListener(actionPerformed);
        mul.setOnClickListener(actionPerformed);
        sub.setOnClickListener(actionPerformed);
        add.setOnClickListener(actionPerformed);
        equal.setOnClickListener(actionPerformed);
        sin.setOnClickListener(actionPerformed);
        cos.setOnClickListener(actionPerformed);
        tan.setOnClickListener(actionPerformed);
        log.setOnClickListener(actionPerformed);
        ln.setOnClickListener(actionPerformed);
        sqrt.setOnClickListener(actionPerformed);
        square.setOnClickListener(actionPerformed);
        factorial.setOnClickListener(actionPerformed);  //阶乘
        bksp.setOnClickListener(actionPerformed);
        left.setOnClickListener(actionPerformed);
        right.setOnClickListener(actionPerformed);
        dot.setOnClickListener(actionPerformed);
        bd.setOnClickListener(actionPerformed);     //2->10
        db.setOnClickListener(actionPerformed);     //10->2/
        drg.setOnClickListener(actionPerformed);    // °
        mc.setOnClickListener(actionPerformed);
        c.setOnClickListener(actionPerformed);
    }

    /**
     * 键盘命令捕捉
     */
    String[] TipCommand = new String[500];
    int tip_i = 0;// TipCommand的指针
    private View.OnClickListener actionPerformed = new View.OnClickListener() {

        public void onClick(View v) {
            // 按键上的命令获取
            String command = ((Button) v).getText().toString();
            // 显示器上的字符串
            String str = input.getText().toString();
            // 检测输入是否合法
            if (equals_flag == false
                    && "0123456789.()sincostanlnlogn!+-×÷√^".indexOf(command) != -1) {
                // 检测显示器上的字符串是否合法
                if (right(str)) {
                    if ("+-×÷√^)".indexOf(command) != -1) {
                        for (int i = 0; i < str.length(); i++) {
                            TipCommand[tip_i] = String.valueOf(str.charAt(i));
                            tip_i++;
                        }
                        vbegin = false;
                    }
                } else {
                    input.setText("0");
                    vbegin = true;
                    tip_i = 0;
                    tip_lock = true;
                    tip.setText("welcome use Lp's APP！");
                }

                equals_flag = true;
            }
            if (tip_i > 0)
                TipChecker(TipCommand[tip_i - 1], command);
            else if (tip_i == 0) {
                TipChecker("#", command);
            }
            if ("0123456789.()sincostanlnlogn!+-×÷√^".indexOf(command) != -1
                    && tip_lock) {
                TipCommand[tip_i] = command;
                tip_i++;
            }
            // 若输入正确，则将输入信息显示到显示器上
            if ("0123456789.()sincostanlnlogn!+-×÷√^".indexOf(command) != -1
                    && tip_lock) { // 共25个按键
                print(command);
                // 若果点击了“DRG”，则切换当前弧度角度制，并将切换后的结果显示到按键上方。
            } else if (command.compareTo("DRG") == 0 && tip_lock) {
                if (drg_flag == true) {
                    drg_flag = false;
                    _drg.setText("RAD");
                } else {
                    drg_flag = true;
                    _drg.setText("DEG");
                }
                // 如果输入时退格键，并且是在按=之前
            } else if (command.compareTo("◁") == 0 && equals_flag) {
                // 一次删除3个字符
                if (TTO(str) == 3) {
                    if (str.length() > 3)
                        input.setText(str.substring(0, str.length() - 3));
                    else if (str.length() == 3) {
                        input.setText("0");
                        vbegin = true;
                        tip_i = 0;
                        tip.setText("welcome use Lp's APP！");
                    }
                    // 依次删除2个字符
                } else if (TTO(str) == 2) {
                    if (str.length() > 2)
                        input.setText(str.substring(0, str.length() - 2));
                    else if (str.length() == 2) {
                        input.setText("0");
                        vbegin = true;
                        tip_i = 0;
                        tip.setText("welcome use Lp's APP！");
                    }
                    // 依次删除一个字符
                } else if (TTO(str) == 1) {
                    // 若之前输入的字符串合法则删除一个字符
                    if (right(str)) {
                        if (str.length() > 1)
                            input.setText(str.substring(0, str.length() - 1));
                        else if (str.length() == 1) {
                            input.setText("0");
                            vbegin = true;
                            tip_i = 0;
                            tip.setText("welcome use Lp's APP！");
                        }
                        // 若之前输入的字符串不合法则删除全部字符
                    } else {
                        input.setText("0");
                        vbegin = true;
                        tip_i = 0;
                        tip.setText("welcome use Lp's APP！");
                    }
                }
                if (input.getText().toString().compareTo("-") == 0
                        || equals_flag == false) {
                    input.setText("0");
                    vbegin = true;
                    tip_i = 0;
                    tip.setText("welcome use Lp's APP！");
                }
                tip_lock = true;
                if (tip_i > 0)
                    tip_i--;
                // 如果是在按=之后输入退格键
            } else if (command.compareTo("Bksp") == 0 && equals_flag == false) {
                // 将显示器内容设置为0
                input.setText("0");
                vbegin = true;
                tip_i = 0;
                tip_lock = true;
                tip.setText("welcome use Lp's APP！");
                // 如果输入的是清除键
            } else if (command.compareTo("C") == 0) {
                // 将显示器内容设置为0
                input.setText("0");
                // 重新输入标志置为true
                vbegin = true;
                // 缓存命令位数清0
                tip_i = 0;
                // 表明可以继续输入
                tip_lock = true;
                // 表明输入=之前
                equals_flag = true;
                tip.setText("welcome use Lp's APP！");
                // 如果输入的是”MC“，则将存储器内容清0
            } else if (command.compareTo("MC") == 0) {
                mem.setText("0");
                // 如果按”bd“则转换进制
            } else if (command.compareTo("B") == 0) {
                String s = input.getText().toString();
                s = s.replace(".","");
                //非法字符清屏
                if(".+-x÷√^sincostanloglnn!()".indexOf(s) != -1){
                    input.setText("0");
                }else
                {
                    try {
                        int a = Integer.valueOf(s);
                        String re = Integer.toBinaryString(a);
                        input.setText(re);
                    }catch (Exception e){
                        input.setText("");
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }

                }
                // 如果按”db“则转换进制
            }else if(command.compareTo("D") == 0){
                String s = input.getText().toString();
                s = s.replace(".","");
                input.setText("1234567890");
                //非法字符清屏
                if("23456789+-x÷√^sincostanloglnn!().".indexOf(s) != -1){
                    input.setText("0");
                }else{
                    try{
                        Integer re = Integer.parseInt(s,2);
                        input.setText(re.toString());
                    }catch (Exception e){
                        input.setText("");
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (command.compareTo("=") == 0 && tip_lock && right(str)
                    && equals_flag) {
                tip_i = 0;
                // 表明不可以继续输入
                tip_lock = false;
                // 表明输入=之后
                equals_flag = false;
                // 保存原来算式样子
                str_old = str;
                // 替换算式中的运算符，便于计算
                str = str.replaceAll("sin", "s");
                str = str.replaceAll("cos", "c");
                str = str.replaceAll("tan", "t");
                str = str.replaceAll("log", "g");
                str = str.replaceAll("ln", "l");
                str = str.replaceAll("n!", "!");
                // 重新输入标志设置true
                vbegin = true;
                // 将-1x转换成-
                str_new = str.replaceAll("-", "-1×");
                // 计算算式结果
                new calc().process(str_new);
            }
            // 表明可以继续输入
            tip_lock = true;
        }
    };

    /*
     * 检测函数，对str进行前后语法检测 为Tip的提示方式提供依据，与TipShow()配合使用 编号 字符 其后可以跟随的合法字符 1 （
     * 数字|（|-|.|函数 2 ） 算符|）|√ ^ 3 . 数字|算符|）|√ ^ 4 数字 .|数字|算符|）|√ ^ 5 算符
     * 数字|（|.|函数 6 √ ^ （ |. | 数字 7 函数 数字|（|.
     *
     * 小数点前后均可省略，表示0 数字第一位可以为0
     */
    private void TipChecker(String tipcommand1, String tipcommand2) {

        // Tipcode1表示错误类型，Tipcode2表示名词解释类型
        int Tipcode1 = 0, Tipcode2 = 0;
        // 表示命令类型
        int tiptype1 = 0, tiptype2 = 0;
        // 括号数
        int bracket = 0;
        // “+-x÷√^”不能作为第一位
        if (tipcommand1.compareTo("#") == 0
                && (tipcommand2.compareTo("÷") == 0
                || tipcommand2.compareTo("×") == 0
                || tipcommand2.compareTo("+") == 0
                || tipcommand2.compareTo(")") == 0
                || tipcommand2.compareTo("√") == 0 || tipcommand2
                .compareTo("^") == 0)) {
            Tipcode1 = -1;
        }
        // 定义存储字符串中最后一位的类型
        else if (tipcommand1.compareTo("#") != 0) {
            if (tipcommand1.compareTo("(") == 0) {
                tiptype1 = 1;
            } else if (tipcommand1.compareTo(")") == 0) {
                tiptype1 = 2;
            } else if (tipcommand1.compareTo(".") == 0) {
                tiptype1 = 3;
            } else if ("0123456789".indexOf(tipcommand1) != -1) {
                tiptype1 = 4;
            } else if ("+-×÷".indexOf(tipcommand1) != -1) {
                tiptype1 = 5;
            } else if ("√^".indexOf(tipcommand1) != -1) {
                tiptype1 = 6;
            } else if ("sincostanloglnn!".indexOf(tipcommand1) != -1) {
                tiptype1 = 7;
            }
            // 定义欲输入的按键类型
            if (tipcommand2.compareTo("(") == 0) {
                tiptype2 = 1;
            } else if (tipcommand2.compareTo(")") == 0) {
                tiptype2 = 2;
            } else if (tipcommand2.compareTo(".") == 0) {
                tiptype2 = 3;
            } else if ("0123456789".indexOf(tipcommand2) != -1) {
                tiptype2 = 4;
            } else if ("+-×÷".indexOf(tipcommand2) != -1) {
                tiptype2 = 5;
            } else if ("√^".indexOf(tipcommand2) != -1) {
                tiptype2 = 6;
            } else if ("sincostanloglnn!".indexOf(tipcommand2) != -1) {
                tiptype2 = 7;
            }

            switch (tiptype1) {
                case 1:
                    // 左括号后面直接接右括号,“+x÷”（负号“-”不算）,或者"√^"
                    if (tiptype2 == 2
                            || (tiptype2 == 5 && tipcommand2.compareTo("-") != 0)
                            || tiptype2 == 6)
                        Tipcode1 = 1;
                    break;
                case 2:
                    // 右括号后面接左括号，数字，“+-x÷sin^...”
                    if (tiptype2 == 1 || tiptype2 == 3 || tiptype2 == 4
                            || tiptype2 == 7)
                        Tipcode1 = 2;
                    break;
                case 3:
                    // “.”后面接左括号或者“sincos...”
                    if (tiptype2 == 1 || tiptype2 == 7)
                        Tipcode1 = 3;
                    // 连续输入两个“.”
                    if (tiptype2 == 3)
                        Tipcode1 = 8;
                    break;
                case 4:
                    // 数字后面直接接左括号或者“sincos...”
                    if (tiptype2 == 1 || tiptype2 == 7)
                        Tipcode1 = 4;
                    break;
                case 5:
                    // “+-x÷”后面直接接右括号，“+-x÷√^”
                    if (tiptype2 == 2 || tiptype2 == 5 || tiptype2 == 6)
                        Tipcode1 = 5;
                    break;
                case 6:
                    // “√^”后面直接接右括号，“+-x÷√^”以及“sincos...”
                    if (tiptype2 == 2 || tiptype2 == 5 || tiptype2 == 6
                            || tiptype2 == 7)
                        Tipcode1 = 6;
                    break;
                case 7:
                    // “sincos...”后面直接接右括号“+-x÷√^”以及“sincos...”
                    if (tiptype2 == 2 || tiptype2 == 5 || tiptype2 == 6
                            || tiptype2 == 7)
                        Tipcode1 = 7;
                    break;
            }
        }
        // 检测小数点的重复性，Tipconde1=0,表明满足前面的规则
        if (Tipcode1 == 0 && tipcommand2.compareTo(".") == 0) {
            int tip_point = 0;
            for (int i = 0; i < tip_i; i++) {
                // 若之前出现一个小数点点，则小数点计数加1
                if (TipCommand[i].compareTo(".") == 0) {
                    tip_point++;
                }
                // 若出现以下几个运算符之一，小数点计数清零
                if (TipCommand[i].compareTo("sin") == 0
                        || TipCommand[i].compareTo("cos") == 0
                        || TipCommand[i].compareTo("tan") == 0
                        || TipCommand[i].compareTo("log") == 0
                        || TipCommand[i].compareTo("ln") == 0
                        || TipCommand[i].compareTo("n!") == 0
                        || TipCommand[i].compareTo("√") == 0
                        || TipCommand[i].compareTo("^") == 0
                        || TipCommand[i].compareTo("÷") == 0
                        || TipCommand[i].compareTo("×") == 0
                        || TipCommand[i].compareTo("-") == 0
                        || TipCommand[i].compareTo("+") == 0
                        || TipCommand[i].compareTo("(") == 0
                        || TipCommand[i].compareTo(")") == 0) {
                    tip_point = 0;
                }
            }
            tip_point++;
            // 若小数点计数大于1，表明小数点重复了
            if (tip_point > 1) {
                Tipcode1 = 8;
            }
        }
        // 检测右括号是否匹配
        if (Tipcode1 == 0 && tipcommand2.compareTo(")") == 0) {
            int tip_right_bracket = 0;
            for (int i = 0; i < tip_i; i++) {
                // 如果出现一个左括号，则计数加1
                if (TipCommand[i].compareTo("(") == 0) {
                    tip_right_bracket++;
                }
                // 如果出现一个右括号，则计数减1
                if (TipCommand[i].compareTo(")") == 0) {
                    tip_right_bracket--;
                }
            }
            // 如果右括号计数=0,表明没有响应的左括号与当前右括号匹配
            if (tip_right_bracket == 0) {
                Tipcode1 = 10;
            }
        }
        // 检查输入=的合法性
        if (Tipcode1 == 0 && tipcommand2.compareTo("=") == 0) {
            // 括号匹配数
            int tip_bracket = 0;
            for (int i = 0; i < tip_i; i++) {
                if (TipCommand[i].compareTo("(") == 0) {
                    tip_bracket++;
                }
                if (TipCommand[i].compareTo(")") == 0) {
                    tip_bracket--;
                }
            }
            // 若大于0，表明左括号还有未匹配的
            if (tip_bracket > 0) {
                Tipcode1 = 9;
                bracket = tip_bracket;
            } else if (tip_bracket == 0) {
                // 若前一个字符是以下之一，表明=号不合法
                if ("√^sincostanloglnn!".indexOf(tipcommand1) != -1) {
                    Tipcode1 = 6;
                }
                // 若前一个字符是以下之一，表明=号不合法
                if ("+-×÷".indexOf(tipcommand1) != -1) {
                    Tipcode1 = 5;
                }
            }
        }
        // 若命令式以下之一，则显示相应的帮助信息
        if (tipcommand2.compareTo("MC") == 0)
            Tipcode2 = 1;
        if (tipcommand2.compareTo("C") == 0)
            Tipcode2 = 2;
        if (tipcommand2.compareTo("DRG") == 0)
            Tipcode2 = 3;
        if (tipcommand2.compareTo("Bksp") == 0)
            Tipcode2 = 4;
        if (tipcommand2.compareTo("sin") == 0)
            Tipcode2 = 5;
        if (tipcommand2.compareTo("cos") == 0)
            Tipcode2 = 6;
        if (tipcommand2.compareTo("tan") == 0)
            Tipcode2 = 7;
        if (tipcommand2.compareTo("log") == 0)
            Tipcode2 = 8;
        if (tipcommand2.compareTo("ln") == 0)
            Tipcode2 = 9;
        if (tipcommand2.compareTo("n!") == 0)
            Tipcode2 = 10;
        if (tipcommand2.compareTo("√") == 0)
            Tipcode2 = 11;
        if (tipcommand2.compareTo("^") == 0)
            Tipcode2 = 12;
        // 显示帮助和错误信息
        TipShow(bracket, Tipcode1, Tipcode2, tipcommand1, tipcommand2);

    }

    /*
     * 反馈Tip信息，加强人机交互，与TipChecker()配合使用
     */
    private void TipShow(int bracket, int tipcode1, int tipcode2,String tipcommand1, String tipcommand2) {

        String tipmessage = "";
        if (tipcode1 != 0)
            tip_lock = false;// 表明输入有误
        switch (tipcode1) {
            case -1:
                tipmessage = tipcommand2 + "  不能作为第一个算符\n";
                break;
            case 1:
                tipmessage = tipcommand1 + "  后应输入：数字/(/./-/函数 \n";
                break;
            case 2:
                tipmessage = tipcommand1 + "  后应输入：)/算符 \n";
                break;
            case 3:
                tipmessage = tipcommand1 + "  后应输入：)/数字/算符 \n";
                break;
            case 4:
                tipmessage = tipcommand1 + "  后应输入：)/./数字 /算符 \n";
                break;
            case 5:
                tipmessage = tipcommand1 + "  后应输入：(/./数字/函数 \n";
                break;
            case 6:
                tipmessage = tipcommand1 + "  后应输入：(/./数字 \n";
                break;
            case 7:
                tipmessage = tipcommand1 + "  后应输入：(/./数字 \n";
                break;
            case 8:
                tipmessage = "小数点重复\n";
                break;
            case 9:
                tipmessage = "不能计算，缺少 " + bracket + " 个 )";
                break;
            case 10:
                tipmessage = "不需要  )";
                break;
        }
        switch (tipcode2) {
            case 1:
                tipmessage = tipmessage + "[MC 用法: 清除记忆 MEM]";
                break;
            case 2:
                tipmessage = tipmessage + "[C 用法: 归零]";
                break;
            case 3:
                tipmessage = tipmessage + "[DRG 用法: 选择 DEG 或 RAD]";
                break;
            case 4:
                tipmessage = tipmessage + "[Bksp 用法: 退格]";
                break;
            case 5:
                tipmessage = tipmessage + "sin 函数用法示例：\n"
                        + "DEG：sin30 = 0.5      RAD：sin1 = 0.84\n"
                        + "注：与其他函数一起使用时要加括号，如：\n" + "sin(cos45)，而不是sincos45";
                break;
            case 6:
                tipmessage = tipmessage + "cos 函数用法示例：\n"
                        + "DEG：cos60 = 0.5      RAD：cos1 = 0.54\n"
                        + "注：与其他函数一起使用时要加括号，如：\n" + "cos(sin45)，而不是cossin45";
                break;
            case 7:
                tipmessage = tipmessage + "tan 函数用法示例：\n"
                        + "DEG：tan45 = 1      RAD：tan1 = 1.55\n"
                        + "注：与其他函数一起使用时要加括号，如：\n" + "tan(cos45)，而不是tancos45";
                break;
            case 8:
                tipmessage = tipmessage + "log 函数用法示例：\n"
                        + "log10 = log(5+5) = 1\n" + "注：与其他函数一起使用时要加括号，如：\n"
                        + "log(tan45)，而不是logtan45";
                break;
            case 9:
                tipmessage = tipmessage + "ln 函数用法示例：\n"
                        + "ln10 = le(5+5) = 2.3   lne = 1\n"
                        + "注：与其他函数一起使用时要加括号，如：\n" + "ln(tan45)，而不是lntan45";
                break;
            case 10:
                tipmessage = tipmessage + "n! 函数用法示例：\n"
                        + "n!3 = n!(1+2) = 3×2×1 = 6\n" + "注：与其他函数一起使用时要加括号，如：\n"
                        + "n!(log1000)，而不是n!log1000";
                break;
            case 11:
                tipmessage = tipmessage + "√ 用法示例：开任意次根号\n"
                        + "如：27开3次根为  27√3 = 3\n" + "注：与其他函数一起使用时要加括号，如：\n"
                        + "(函数)√(函数) ， (n!3)√(log100) = 2.45";
                break;
            case 12:
                tipmessage = tipmessage + "^ 用法示例：开任意次平方\n" + "如：2的3次方为  2^3 = 8\n"
                        + "注：与其他函数一起使用时要加括号，如：\n"
                        + "(函数)√(函数) ， (n!3)^(log100) = 36";
                break;
        }
        // 将提示信息显示到tip
        tip.setText(tipmessage);
    }

    /**
     * 将信息显示在显示屏上
     */
    private void print(String str) {
        // 清屏后输出
        if (vbegin) {
            input.setText(str);
        } else {
            input.append(str);
        }
        vbegin = false;
    }

    /*
     * 检测函数，返回值为3、2、1 表示应当一次删除几个？ Three+Two+One = TTO 为Bksp按钮的删除方式提供依据
     * 返回3，表示str尾部为sin、cos、tan、log中的一个，应当一次删除3个 返回2，表示str尾部为ln、n!中的一个，应当一次删除2个
     * 返回1，表示为除返回3、2外的所有情况，只需删除一个（包含非法字符时要另外考虑：应清屏）
     */
    private int TTO(String str) {
        if ((str.charAt(str.length() - 1) == 'n'
                && str.charAt(str.length() - 2) == 'i' && str.charAt(str
                .length() - 3) == 's')
                || (str.charAt(str.length() - 1) == 's'
                && str.charAt(str.length() - 2) == 'o' && str
                .charAt(str.length() - 3) == 'c')
                || (str.charAt(str.length() - 1) == 'n'
                && str.charAt(str.length() - 2) == 'a' && str
                .charAt(str.length() - 3) == 't')
                || (str.charAt(str.length() - 1) == 'g'
                && str.charAt(str.length() - 2) == 'o' && str
                .charAt(str.length() - 3) == 'l')) {
            return 3;
        } else if ((str.charAt(str.length() - 1) == 'n' && str.charAt(str
                .length() - 2) == 'l')
                || (str.charAt(str.length() - 1) == '!' && str.charAt(str
                .length() - 2) == 'n')) {
            return 2;
        } else {
            return 1;
        }
    }

    /*
     * 判断一个str是否是合法的，返回值为true、false
     * 只包含0123456789.()sincostanlnlogn!+-×÷√^的是合法的str，返回true
     * 包含了除0123456789.()sincostanlnlogn!+-×÷√^以外的字符的str为非法的，返回false
     */
    private boolean right(String str) {
        int i = 0;
        for (i = 0; i < str.length(); i++) {
            if (str.charAt(i) != '0' && str.charAt(i) != '1'
                    && str.charAt(i) != '2' && str.charAt(i) != '3'
                    && str.charAt(i) != '4' && str.charAt(i) != '5'
                    && str.charAt(i) != '6' && str.charAt(i) != '7'
                    && str.charAt(i) != '8' && str.charAt(i) != '9'
                    && str.charAt(i) != '.' && str.charAt(i) != '-'
                    && str.charAt(i) != '+' && str.charAt(i) != '×'
                    && str.charAt(i) != '÷' && str.charAt(i) != '√'
                    && str.charAt(i) != '^' && str.charAt(i) != 's'
                    && str.charAt(i) != 'i' && str.charAt(i) != 'n'
                    && str.charAt(i) != 'c' && str.charAt(i) != 'o'
                    && str.charAt(i) != 't' && str.charAt(i) != 'a'
                    && str.charAt(i) != 'l' && str.charAt(i) != 'g'
                    && str.charAt(i) != '(' && str.charAt(i) != ')'
                    && str.charAt(i) != '!')
                break;
        }
        if (i == str.length()) {
            return true;
        } else {
            return false;
        }
    }


    /*
     * 整个计算核心，
     * 只要将表达式的整个字符串传入calc().process()就可以实行计算了 算法包括以下几部分：
     *  1、计算部分
     * process(String str) 当然，这是建立在查错无错误的情况下
     * 2、数据格式化 FP(double n) 使数据有相当的精确度
     * 3、阶乘算法 N(double n) 计算n!，将结果返回
     *  4、错误提示 showError(int code ,String str)
     * 将错误返回
     */
    public class calc {

        public calc() { }

        final int MAXLEN = 500;

        /*
         * 计算表达式 从左向右扫描，数字入number栈，运算符入operator栈
         * +-基本优先级为1，
         * ×÷基本优先级为2，
         * log ln sin cos tan n!基本优先级为3，
         * √^基本优先级为4
         * 括号内层运算符比外层同级运算符优先级高4
         * 当前运算符优先级高于栈顶压栈，
         * 低于栈顶弹出一个运算符与两个数进行运算
         *  重复直到当前运算符大于栈顶
         *   扫描完后对剩下的运算符与数字依次计算
         */
        public void process(String str) {
            int weightPlus = 0, topOp = 0, topNum = 0, flag = 1, weightTemp = 0;
            // weightPlus为同一（）下的基本优先级，weightTemp临时记录优先级的变化
            // topOp为weight[]，operator[]的计数器；topNum为number[]的计数器
            // flag为正负数的计数器，1为正数，-1为负数
            int weight[]; // 保存operator栈中运算符的优先级，以topOp计数
            double number[]; // 保存数字，以topNum计数
            char ch, ch_gai, operator[];// operator[]保存运算符，以topOp计数
            String num;// 记录数字，str以+-×÷()sctg ln ! √ ^分段，+-×÷()sctgl!√^字符之间的字符串即为数字
            weight = new int[MAXLEN];
            number = new double[MAXLEN];
            operator = new char[MAXLEN];
            String expression = str;
            StringTokenizer expToken = new StringTokenizer(expression,
                    "+-×÷()sctgl!√^");
            int i = 0;
            while (i < expression.length()) {
                ch = expression.charAt(i);
                // 判断正负数
                if (i == 0) {
                    if (ch == '-')
                        flag = -1;
                } else if (expression.charAt(i - 1) == '(' && ch == '-')
                    flag = -1;
                // 取得数字，并将正负符号转移给数字
                if (ch <= '9' && ch >= '0' || ch == '.' || ch == 'E') {
                    num = expToken.nextToken();
                    ch_gai = ch;
                    Log.e("guojs", ch + "--->" + i);
                    // 取得整个数字
                    while (i < expression.length()
                            && (ch_gai <= '9' && ch_gai >= '0' || ch_gai == '.' || ch_gai == 'E')) {
                        ch_gai = expression.charAt(i++);
                        Log.e("guojs", "i的值为：" + i);
                    }
                    // 将指针退回之前的位置
                    if (i >= expression.length())
                        i -= 1;
                    else {
                        i -= 2;
                    }
                    if (num.compareTo(".") == 0)
                        number[topNum++] = 0;
                        // 将正负符号转移给数字
                    else {
                        number[topNum++] = Double.parseDouble(num) * flag;
                        flag = 1;
                    }
                }
                // 计算运算符的优先级
                if (ch == '(')
                    weightPlus += 4;
                if (ch == ')')
                    weightPlus -= 4;
                if (ch == '-' && flag == 1 || ch == '+' || ch == '×'
                        || ch == '÷' || ch == 's' || ch == 'c' || ch == 't'
                        || ch == 'g' || ch == 'l' || ch == '!' || ch == '√'
                        || ch == '^') {
                    switch (ch) {
                        // +-的优先级最低，为1
                        case '+':
                        case '-':
                            weightTemp = 1 + weightPlus;
                            break;
                        // x÷的优先级稍高，为2
                        case '×':
                        case '÷':
                            weightTemp = 2 + weightPlus;
                            break;
                        // sincos之类优先级为3
                        case 's':
                        case 'c':
                        case 't':
                        case 'g':
                        case 'l':
                        case '!':
                            weightTemp = 3 + weightPlus;
                            break;
                        // 其余优先级为4
                        // case '^':
                        // case '√':
                        default:
                            weightTemp = 4 + weightPlus;
                            break;
                    }
                    // 如果当前优先级大于堆栈顶部元素，则直接入栈
                    if (topOp == 0 || weight[topOp - 1] < weightTemp) {
                        weight[topOp] = weightTemp;
                        operator[topOp] = ch;
                        topOp++;
                        // 否则将堆栈中运算符逐个取出，直到当前堆栈顶部运算符的优先级小于当前运算符
                    } else {
                        while (topOp > 0 && weight[topOp - 1] >= weightTemp) {
                            switch (operator[topOp - 1]) {
                                // 取出数字数组的相应元素进行运算
                                case '+':
                                    number[topNum - 2] += number[topNum - 1];
                                    break;
                                case '-':
                                    number[topNum - 2] -= number[topNum - 1];
                                    break;
                                case '×':
                                    number[topNum - 2] *= number[topNum - 1];
                                    break;
                                // 判断除数为0的情况
                                case '÷':
                                    if (number[topNum - 1] == 0) {
                                        showError(1, str_old);
                                        return;
                                    }
                                    number[topNum - 2] /= number[topNum - 1];
                                    break;
                                case '√':
                                    if (number[topNum - 1] == 0
                                            || (number[topNum - 2] < 0 && number[topNum - 1] % 2 == 0)) {
                                        showError(2, str_old);
                                        return;
                                    }
                                    number[topNum - 2] = Math.pow(
                                            number[topNum - 2],
                                            1 / number[topNum - 1]);
                                    break;
                                case '^':
                                    number[topNum - 2] = Math.pow(
                                            number[topNum - 2], number[topNum - 1]);
                                    break;
                                // 计算时进行角度弧度的判断及转换
                                // sin
                                case 's':
                                    //drg_flag true--角度  false--弧度
                                    if (drg_flag == true) {
                                        number[topNum - 1] = Math
                                                .sin((number[topNum - 1] / 180)
                                                        * pi);
                                    } else {
                                        number[topNum - 1] = Math
                                                .sin(number[topNum - 1]);
                                    }
                                    topNum++;
                                    break;
                                // cos
                                case 'c':
                                    if (drg_flag == true) {
                                        number[topNum - 1] = Math
                                                .cos((number[topNum - 1] / 180)
                                                        * pi);
                                    } else {
                                        number[topNum - 1] = Math
                                                .cos(number[topNum - 1]);
                                    }
                                    topNum++;
                                    break;
                                // tan
                                case 't':
                                    if (drg_flag == true) {
                                        if ((Math.abs(number[topNum - 1]) / 90) % 2 == 1) {
                                            showError(2, str_old);
                                            return;
                                        }
                                        number[topNum - 1] = Math
                                                .tan((number[topNum - 1] / 180)
                                                        * pi);
                                    } else {
                                        if ((Math.abs(number[topNum - 1]) / (pi / 2)) % 2 == 1) {
                                            showError(2, str_old);
                                            return;
                                        }
                                        number[topNum - 1] = Math
                                                .tan(number[topNum - 1]);
                                    }
                                    topNum++;
                                    break;
                                // log
                                case 'g':
                                    if (number[topNum - 1] <= 0) {
                                        showError(2, str_old);
                                        return;
                                    }
                                    number[topNum - 1] = Math
                                            .log10(number[topNum - 1]);
                                    topNum++;
                                    break;
                                // ln
                                case 'l':
                                    if (number[topNum - 1] <= 0) {
                                        showError(2, str_old);
                                        return;
                                    }
                                    number[topNum - 1] = Math
                                            .log(number[topNum - 1]);
                                    topNum++;
                                    break;
                                // 阶乘
                                case '!':
                                    if (number[topNum - 1] > 170) {
                                        showError(3, str_old);
                                        return;
                                    } else if (number[topNum - 1] < 0) {
                                        showError(2, str_old);
                                        return;
                                    }
                                    number[topNum - 1] = N(number[topNum - 1]);
                                    topNum++;
                                    break;
                            }
                            // 继续取堆栈的下一个元素进行判断
                            topNum--;
                            topOp--;
                        }
                        // 将运算符如堆栈
                        weight[topOp] = weightTemp;
                        operator[topOp] = ch;
                        topOp++;
                    }
                }
                i++;
            }
            // 依次取出堆栈的运算符进行运算
            while (topOp > 0) {
                // +-x直接将数组的后两位数取出运算
                switch (operator[topOp - 1]) {
                    case '+':
                        number[topNum - 2] += number[topNum - 1];
                        break;
                    case '-':
                        number[topNum - 2] -= number[topNum - 1];
                        break;
                    case '×':
                        number[topNum - 2] *= number[topNum - 1];
                        break;
                    // 涉及到除法时要考虑除数不能为零的情况
                    case '÷':
                        if (number[topNum - 1] == 0) {
                            showError(1, str_old);
                            return;
                        }
                        number[topNum - 2] /= number[topNum - 1];
                        break;
                    case '√':
                        if (number[topNum - 1] == 0
                                || (number[topNum - 2] < 0 && number[topNum - 1] % 2 == 0)) {
                            showError(2, str_old);
                            return;
                        }
                        number[topNum - 2] = Math.pow(number[topNum - 2],
                                1 / number[topNum - 1]);
                        break;
                    case '^':
                        number[topNum - 2] = Math.pow(number[topNum - 2],
                                number[topNum - 1]);
                        break;
                    // sin
                    case 's':
                        if (drg_flag == true) {
                            number[topNum - 1] = Math
                                    .sin((number[topNum - 1] / 180) * pi);
                        } else {
                            number[topNum - 1] = Math.sin(number[topNum - 1]);
                        }
                        topNum++;
                        break;
                    // cos
                    case 'c':
                        if (drg_flag == true) {
                            number[topNum - 1] = Math
                                    .cos((number[topNum - 1] / 180) * pi);
                        } else {
                            number[topNum - 1] = Math.cos(number[topNum - 1]);
                        }
                        topNum++;
                        break;
                    // tan
                    case 't':
                        if (drg_flag == true) {
                            if ((Math.abs(number[topNum - 1]) / 90) % 2 == 1) {
                                showError(2, str_old);
                                return;
                            }
                            number[topNum - 1] = Math
                                    .tan((number[topNum - 1] / 180) * pi);
                        } else {
                            if ((Math.abs(number[topNum - 1]) / (pi / 2)) % 2 == 1) {
                                showError(2, str_old);
                                return;
                            }
                            number[topNum - 1] = Math.tan(number[topNum - 1]);
                        }
                        topNum++;
                        break;
                    // 对数log
                    case 'g':
                        if (number[topNum - 1] <= 0) {
                            showError(2, str_old);
                            return;
                        }
                        number[topNum - 1] = Math.log10(number[topNum - 1]);
                        topNum++;
                        break;
                    // 自然对数ln
                    case 'l':
                        if (number[topNum - 1] <= 0) {
                            showError(2, str_old);
                            return;
                        }
                        number[topNum - 1] = Math.log(number[topNum - 1]);
                        topNum++;
                        break;
                    // 阶乘
                    case '!':
                        if (number[topNum - 1] > 170) {
                            showError(3, str_old);
                            return;
                        } else if (number[topNum - 1] < 0) {
                            showError(2, str_old);
                            return;
                        }
                        number[topNum - 1] = N(number[topNum - 1]);
                        topNum++;
                        break;
                }
                // 取堆栈下一个元素计算
                topNum--;
                topOp--;
            }
            // 如果是数字太大，提示错误信息
            if (number[0] > 7.3E306) {
                showError(3, str_old);
                return;
            }
            // 输出最终结果
            input.setText(String.valueOf(FP(number[0])));
            tip.setText("计算完毕，要继续请按归零键 C");
            mem.setText(str_old + "=" + String.valueOf(FP(number[0])));
        }

        /*
         * FP = floating point 控制小数位数，达到精度 否则会出现
         * 0.6-0.2=0.39999999999999997的情况，用FP即可解决，使得数为0.4 本格式精度为15位
         */
        public double FP(double n) {
            // NumberFormat format=NumberFormat.getInstance(); //创建一个格式化类f
            // format.setMaximumFractionDigits(18); //设置小数位的格式
//            DecimalFormat format = new DecimalFormat("0.#############");
            Double re = n;
            String s = round(re.toString(),DEF_DIV_SCALE[0]);
            return Double.parseDouble(s);
        }

        /*
         * 阶乘算法
         */
        public double N(double n) {
            int i = 0;
            double sum = 1;
            // 依次将小于等于n的值相乘
            for (i = 1; i <= n; i++) {
                sum = sum * i;
            }
            return sum;
        }

        /*
         * 错误提示，按了"="之后，若计算式在process()过程中，出现错误，则进行提示
         */
        public void showError(int code, String str) {
            String message = "";
            switch (code) {
                case 1:
                    message = "零不能作除数";
                    break;
                case 2:
                    message = "函数格式错误";
                    break;
                case 3:
                    message = "值太大了，超出范围";
            }
            input.setText("\"" + str + "\"" + ": " + message);
            tip.setText(message + "\n" + "计算完毕，要继续请按归零键 C");
        }
    }

    /**
     *保存切换页面时EditEext中的值
     */
    @Override   //这种方式保存数据只是临时保存，无法永久保存
    protected void onSaveInstanceState(@NonNull Bundle outState) {  //该函数用于提取出历史数据，保存到outState中
        super.onSaveInstanceState(outState);
        init[0] = input.getText().toString();
        outState.putString("exp",init[0]);
    }

    /**
     *舍入精度
     */
    public static String round(String v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(v);
        BigDecimal one = new BigDecimal("1");

        BigDecimal re = b.divide(one, scale, BigDecimal.ROUND_HALF_UP);

        return re.toString();
    }

    /**
     *音量键事件
     */
    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {

        String s = input.getText().toString();
        if(s.length()<1){

        }else {
            switch (keyCode) {
                // 音量减小
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    if(DEF_DIV_SCALE[0]>1)
                        DEF_DIV_SCALE[0]--;
                    seekBar.setProgress(DEF_DIV_SCALE[0]);
                    Toast.makeText(this.getApplicationContext(), "DOWN " + DEF_DIV_SCALE[0], Toast.LENGTH_SHORT).show();
                    if(s.contains(".")) input.setText(round(s,DEF_DIV_SCALE[0]));
                    // 音量减小时应该执行的功能代码
                    return true;

                // 音量增大
                case KeyEvent.KEYCODE_VOLUME_UP:
                    if(DEF_DIV_SCALE[0]<40)
                        DEF_DIV_SCALE[0]++;
                    seekBar.setProgress(DEF_DIV_SCALE[0]);
                    Toast.makeText(this.getApplicationContext(), "UP " + DEF_DIV_SCALE[0], Toast.LENGTH_SHORT).show();
                    if(s.contains(".")) input.setText(round(s,DEF_DIV_SCALE[0]));
                    // 音量增大时应该执行的功能代码
                    return true;
            }
        }
        return super.onKeyDown (keyCode, event);
    }

    /**
     * 滑动条事件
     */
    public SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //txt_cur.setText("当前进度值:" + progress + "  / 100 ");
            String s = input.getText().toString();
            if(s.length()<1){
                return;
            }else{
                if(progress>DEF_DIV_SCALE[0]){
                    DEF_DIV_SCALE[0] = progress;
                    Toast.makeText(getApplicationContext(), "UP " + DEF_DIV_SCALE[0], Toast.LENGTH_SHORT).show();
                    if(s.contains(".")) input.setText(round(s,DEF_DIV_SCALE[0]));
                }else if(progress<DEF_DIV_SCALE[0]){
                    DEF_DIV_SCALE[0] = progress;
                    Toast.makeText(getApplicationContext(), "DOWN " + DEF_DIV_SCALE[0], Toast.LENGTH_SHORT).show();
                    if(s.contains(".")) input.setText(round(s,DEF_DIV_SCALE[0]));
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //Toast.makeText(mContext, "触碰SeekBar", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //Toast.makeText(mContext, "放开SeekBar", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 二进制2十进制
     */
    public int BinaryToDecimal(String s){
        int binaryNumber = Integer.valueOf(s);
        int decimal = 0;
        int p = 0;
        while(true){
            if(binaryNumber == 0){
                break;
            } else {
                int temp = binaryNumber%10;
                decimal += temp*Math.pow(2, p);
                binaryNumber = binaryNumber/10;
                p++;
            }
        }
        return decimal;
    }

    /**
     *十进制2二进制
     */

}