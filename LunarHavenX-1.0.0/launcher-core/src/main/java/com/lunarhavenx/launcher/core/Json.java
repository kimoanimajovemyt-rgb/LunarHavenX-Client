package com.lunarhavenx.launcher.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Parser/escritor JSON mínimo, sem dependências (funciona em desktop e Android). */
public final class Json {
    private final String s;
    private int i;

    private Json(String s) { this.s = s; }

    public static Object parse(String text) {
        Json j = new Json(text);
        j.ws();
        Object v = j.value();
        j.ws();
        if (j.i != j.s.length()) throw j.err("conteúdo extra após o JSON");
        return v;
    }

    private IllegalArgumentException err(String m) { return new IllegalArgumentException("JSON inválido (pos " + i + "): " + m); }
    private void ws() { while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++; }
    private char peek() { if (i >= s.length()) throw err("fim inesperado"); return s.charAt(i); }

    private Object value() {
        switch (peek()) {
            case '{': return object();
            case '[': return array();
            case '"': return string();
            case 't': expect("true"); return Boolean.TRUE;
            case 'f': expect("false"); return Boolean.FALSE;
            case 'n': expect("null"); return null;
            default: return number();
        }
    }

    private void expect(String w) { if (!s.startsWith(w, i)) throw err("esperado " + w); i += w.length(); }

    private Map<String, Object> object() {
        i++;
        Map<String, Object> m = new LinkedHashMap<>();
        ws();
        if (peek() == '}') { i++; return m; }
        while (true) {
            ws();
            String k = string();
            ws();
            if (peek() != ':') throw err("esperado ':'");
            i++;
            ws();
            m.put(k, value());
            ws();
            char c = peek(); i++;
            if (c == '}') return m;
            if (c != ',') throw err("esperado ',' ou '}'");
        }
    }

    private List<Object> array() {
        i++;
        List<Object> l = new ArrayList<>();
        ws();
        if (peek() == ']') { i++; return l; }
        while (true) {
            ws();
            l.add(value());
            ws();
            char c = peek(); i++;
            if (c == ']') return l;
            if (c != ',') throw err("esperado ',' ou ']'");
        }
    }

    private String string() {
        if (peek() != '"') throw err("esperado string");
        i++;
        StringBuilder b = new StringBuilder();
        while (true) {
            char c = peek(); i++;
            if (c == '"') return b.toString();
            if (c != '\\') { b.append(c); continue; }
            char e = peek(); i++;
            switch (e) {
                case 'n': b.append('\n'); break;
                case 't': b.append('\t'); break;
                case 'r': b.append('\r'); break;
                case 'b': b.append('\b'); break;
                case 'f': b.append('\f'); break;
                case 'u':
                    if (i + 4 > s.length()) throw err("escape \\u incompleto");
                    b.append((char) Integer.parseInt(s.substring(i, i + 4), 16));
                    i += 4;
                    break;
                default: b.append(e);
            }
        }
    }

    private Object number() {
        int st = i;
        while (i < s.length() && "+-0123456789.eE".indexOf(s.charAt(i)) >= 0) i++;
        if (st == i) throw err("valor inesperado");
        String n = s.substring(st, i);
        if (n.indexOf('.') >= 0 || n.indexOf('e') >= 0 || n.indexOf('E') >= 0) return Double.valueOf(n);
        return Long.valueOf(n);
    }

    // ---------- escrita ----------
    public static String write(Object o) {
        StringBuilder b = new StringBuilder();
        write(b, o, 0);
        return b.toString();
    }

    private static void indent(StringBuilder b, int n) { b.append('\n'); for (int k = 0; k < n; k++) b.append("  "); }

    @SuppressWarnings("unchecked")
    private static void write(StringBuilder b, Object o, int d) {
        if (o == null) b.append("null");
        else if (o instanceof String) quote(b, (String) o);
        else if (o instanceof Boolean || o instanceof Number) b.append(o);
        else if (o instanceof Map) {
            Map<String, Object> m = (Map<String, Object>) o;
            if (m.isEmpty()) { b.append("{}"); return; }
            b.append('{');
            boolean first = true;
            for (Map.Entry<String, Object> e : m.entrySet()) {
                if (!first) b.append(',');
                first = false;
                indent(b, d + 1);
                quote(b, e.getKey());
                b.append(": ");
                write(b, e.getValue(), d + 1);
            }
            indent(b, d);
            b.append('}');
        } else if (o instanceof List) {
            List<Object> l = (List<Object>) o;
            if (l.isEmpty()) { b.append("[]"); return; }
            b.append('[');
            boolean first = true;
            for (Object x : l) {
                if (!first) b.append(',');
                first = false;
                indent(b, d + 1);
                write(b, x, d + 1);
            }
            indent(b, d);
            b.append(']');
        } else throw new IllegalArgumentException("tipo não suportado: " + o.getClass());
    }

    private static void quote(StringBuilder b, String s) {
        b.append('"');
        for (int k = 0; k < s.length(); k++) {
            char c = s.charAt(k);
            switch (c) {
                case '"': b.append("\\\""); break;
                case '\\': b.append("\\\\"); break;
                case '\n': b.append("\\n"); break;
                case '\r': b.append("\\r"); break;
                case '\t': b.append("\\t"); break;
                default:
                    if (c < 0x20) b.append(String.format("\\u%04x", (int) c)); else b.append(c);
            }
        }
        b.append('"');
    }

    // ---------- helpers ----------
    @SuppressWarnings("unchecked")
    public static Map<String, Object> obj(Object o) { return o instanceof Map ? (Map<String, Object>) o : null; }

    @SuppressWarnings("unchecked")
    public static List<Object> list(Object o) { return o instanceof List ? (List<Object>) o : null; }

    /** Navega por chaves aninhadas; retorna null se algum nível não existir. */
    public static Object path(Object root, String... keys) {
        Object cur = root;
        for (String k : keys) {
            Map<String, Object> m = obj(cur);
            if (m == null) return null;
            cur = m.get(k);
        }
        return cur;
    }

    public static String str(Object root, String... keys) {
        Object v = path(root, keys);
        return v == null ? null : v.toString();
    }

    public static long num(Object root, long def, String... keys) {
        Object v = path(root, keys);
        return v instanceof Number ? ((Number) v).longValue() : def;
    }
}
