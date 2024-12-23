/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space;

import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.glfw.GLFW;
import space.hack.Hack;
import space.hack.hud.Hud;
import space.manager.EventsHandler;
import space.manager.HackManager;
import space.value.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class Core {

    public static boolean mode;

    public void initialize() {
        new HackManager();
        MinecraftForge.EVENT_BUS.register(new EventsHandler());

        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(23142);
                System.out.println("Server started. Listening on port 23142...");
                while (true) {
                    Socket socket = serverSocket.accept();

                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                    String inputLine;
                    StringBuilder request = new StringBuilder();
                    SeverMode severmode = new SeverMode();

                    while ((inputLine = in.readLine()) != null) {
                        if (inputLine.isEmpty()) {
                            break;
                        }
                        request.append(inputLine).append("\r\n");
                        int separatorIndex = inputLine.indexOf(":");
                        if (separatorIndex > 0) {
                            String name = inputLine.substring(0, separatorIndex).trim();
                            String value = inputLine.substring(separatorIndex + 1).trim();
                            severmode.add(name, value);
                        }
                    }

                    try {
                        StringTokenizer tokenizer = new StringTokenizer(request.toString());
                        String method = tokenizer.nextToken().toUpperCase();
                        String httpPath = tokenizer.nextToken();
                        severmode.httpPath = httpPath;
                        System.out.println("HelloSpace: " + httpPath);

                        if (method.equals("GET")) {
                            out.print("HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n" + Disposal(httpPath));
                        } else if (method.equals("POST")) {
                            out.print("HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n" + Disposal(severmode));
                        }
                    } catch (NoSuchElementException ex) {
                        ex.fillInStackTrace();
                    }

                    out.flush();
                    socket.close();
                }
            } catch (IOException ex) {
                ex.fillInStackTrace();
            }
        }).start();
    }

    // Get
    public static String Disposal(final String httpPath) {
        switch (httpPath) {
            case "/hello" -> {
                return "Hello-Space";
            }
            case "/list" -> {
                StringBuilder list = new StringBuilder();
                for (final Hack h : HackManager.getHack()) {
                    list.append("[#(").append(h.getName()).append("[#&&#]").append("[").append(h.isToggled())
                            .append("]").append(h.isCategory().toString()).append(")#][#@@#]");
                }
                return list.toString();
            }
            case "/config", "/visual" -> {
                return getConfig(1, httpPath.equals("/visual")) + getConfig(2, httpPath.equals("/visual"));
            }
        }
        return "https://npyyds.top/";
    }

    public static String getConfig(int array, boolean visual) {
        StringBuilder list = new StringBuilder();

        boolean isHack;
        ArrayList<Hack> hack = new ArrayList<>();
        ArrayList<Hud> hud = new ArrayList<>();
        ArrayList<HaCd> haCds = new ArrayList<>();

        if (array == 1) {
            isHack = true;
            hack = HackManager.getHack();
            haCds.addAll(hack);
        } else {
            isHack = false;
            hud = HackManager.getHud();
            haCds.addAll(hud);
        }

        for (int i = 0; i < haCds.size(); i++) {
            HaCd h = haCds.get(i);
            StringBuilder text = new StringBuilder();
            text.append("---").append(h.getName()).append("---\n");

            if (isHack && hack.get(i).isToggled() || !isHack && hud.get(i).isToggled()) {
                if (visual) {
                    text.append("Open-true\n");
                } else {
                    text.append("[#(Open[#&&#]true)#][#@@#]");
                }
            }

            int key = h.getKey();
            if (key != 0) {
                if (visual) {
                    text.append("Key-").append(convertKeycodeToString(key)).append("\n");
                }else  {
                    text.append("[#(Key[#&&#]").append(key).append(")#][#@@#]");
                }
            }

            if (!isHack && hud.get(i).x != 0){
                if (visual) {
                    text.append("PosX-").append(hud.get(i).x).append("\n");
                }else {
                    text.append("[#(PosX[#&&#]").append(hud.get(i).x).append(")#][#@@#]");
                }
            }

            if (!isHack && hud.get(i).y != 0){
                if (visual) {
                    text.append("PosY-").append(hud.get(i).y).append("\n");
                }else {
                    text.append("[#(PosY[#&&#]").append(hud.get(i).y).append(")#][#@@#]");
                }
            }

            for (final Value<?> value : h.getValues()) {
                if (!HackManager.isDefault(value)) {
                    text.append(visual ? "" : "[#(").append(value.getName()).append(visual ? "-" : "[#&&#]")
                            .append(value instanceof ModeValue hh ? hh.getMode() : value.getValue())
                            .append(visual ? "\n" : ")#][#@@#]");
                }
            }

            if (!text.toString().equals("---" + h.getName() + "---\n")){
                list.append(text);
            }
        }


        return list.toString();
    }

    public static String convertKeycodeToString(int keycode) {
        return switch (keycode) {
            case GLFW.GLFW_KEY_A -> "A";
            case GLFW.GLFW_KEY_B -> "B";
            case GLFW.GLFW_KEY_C -> "C";
            case GLFW.GLFW_KEY_D -> "D";
            case GLFW.GLFW_KEY_E -> "E";
            case GLFW.GLFW_KEY_F -> "F";
            case GLFW.GLFW_KEY_G -> "G";
            case GLFW.GLFW_KEY_H -> "H";
            case GLFW.GLFW_KEY_I -> "I";
            case GLFW.GLFW_KEY_J -> "J";
            case GLFW.GLFW_KEY_K -> "K";
            case GLFW.GLFW_KEY_L -> "L";
            case GLFW.GLFW_KEY_M -> "M";
            case GLFW.GLFW_KEY_N -> "N";
            case GLFW.GLFW_KEY_O -> "O";
            case GLFW.GLFW_KEY_P -> "P";
            case GLFW.GLFW_KEY_Q -> "Q";
            case GLFW.GLFW_KEY_R -> "R";
            case GLFW.GLFW_KEY_S -> "S";
            case GLFW.GLFW_KEY_T -> "T";
            case GLFW.GLFW_KEY_U -> "U";
            case GLFW.GLFW_KEY_V -> "V";
            case GLFW.GLFW_KEY_W -> "W";
            case GLFW.GLFW_KEY_X -> "X";
            case GLFW.GLFW_KEY_Y -> "Y";
            case GLFW.GLFW_KEY_Z -> "Z";
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> "CTRL";
            case GLFW.GLFW_KEY_RIGHT_ALT -> "ALT";
            case GLFW.GLFW_KEY_KP_0 -> "P0";
            case GLFW.GLFW_KEY_KP_1 -> "P1";
            case GLFW.GLFW_KEY_KP_2 -> "P2";
            case GLFW.GLFW_KEY_KP_3 -> "P3";
            case GLFW.GLFW_KEY_KP_4 -> "P4";
            case GLFW.GLFW_KEY_KP_5 -> "P5";
            case GLFW.GLFW_KEY_KP_6 -> "P6";
            case GLFW.GLFW_KEY_KP_7 -> "P7";
            case GLFW.GLFW_KEY_KP_8 -> "P8";
            case GLFW.GLFW_KEY_KP_9 -> "P9";
            default -> "Error";
        };
    }

    // Post
    public static String Disposal(final SeverMode severmode) {
        if (severmode.isPath("/list")) {
            StringBuilder list = new StringBuilder();
            if (severmode.bool("Mode", "UiGui")) {
                String name = severmode.is("Name");
                for (final Hack h : HackManager.getHack()) {
                    if (h.getName().equals(name)) {
                        for (final Value<?> x : h.getValues()) {
                            if (x instanceof BooleanValue ff) {
                                list.append("BooleanValue").append(ff.getName()).append("[#::#]").append(ff.getValue())
                                        .append("[#@@#]");
                            } else if (x instanceof NumberValue ff) {
                                list.append("NumberValue").append(ff.getName()).append("[#::#]").append(ff.getValue())
                                        .append("[").append("\"").append(ff.getMin()).append("\"").append("-")
                                        .append("\"").append(ff.getMax()).append("\"").append("]").append("[#@@#]");
                            } else if (x instanceof IntValue ff) {
                                list.append("IntValue").append(ff.getName()).append("[#::#]").append(ff.getValue())
                                        .append("[").append("\"").append(ff.getMin()).append("\"").append("-")
                                        .append("\"").append(ff.getMax()).append("\"").append("]").append("[#@@#]");
                            } else if (x instanceof ModeValue modeValue) {
                                list.append("ModeValue").append(modeValue.getName()).append("[#::#]")
                                        .append(modeValue.getMode()).append("[#@@#]");
                            }
                        }
                        list.append("BindValue").append(h.getKey()).append("[#::#]").append("[#@@#]");
                        list.append("HackCategory").append(h.isCategory()).append("[#::#]");
                        list.append("GameIP").append(EventsHandler.gameIp).append("[#::#]");
                        break;
                    }
                }

            } else if (severmode.bool("Mode", "ModeValue")) {
                HaCd hack = HackManager.getSearch(severmode.is("Name"));
                if (hack != null) {
                    String modename = severmode.is("Value");
                    for (final Value<?> x : hack.getValues()) {
                        if (x instanceof ModeValue modeValue) {
                            if (x.getName().equals(modename)) {
                                for (final Mode modeS : modeValue.getModes()) {
                                    if (!modeS.isToggled())
                                        list.append(modeS.getName()).append("[#@@#]");
                                }
                                break;
                            }
                        }
                    }
                }

            } else if (severmode.bool("Mode", "NumberValue")) {
                HaCd hack = HackManager.getSearch(severmode.is("Name"));
                if (hack != null) {
                    String modename = severmode.is("Value");
                    for (final Value<?> x : hack.getValues()) {
                        if (x instanceof NumberValue || x instanceof IntValue) {
                            if (x.getName().equals(modename)) {
                                list.append(x.getValue()).append("[#@@#]");
                                break;
                            }
                        }
                    }
                }

            } else {
                String param1 = severmode.is("Mode");
                for (final Hack h : HackManager.getHack()) {
                    if (h.isCategory().toString().equals(param1)) {
                        list.append("[#(").append(h.getName()).append("[#&&#]").append(h.isToggled())
                                .append(")#][#@@#]");
                    }
                }
            }
            return list.toString();
        } else if (severmode.isPath("/set") || severmode.isPath("/config")) {
            HackManager.severMode.add(severmode);
            return "Ok";
        }
        return "";
    }
}