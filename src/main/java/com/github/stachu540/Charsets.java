package com.github.stachu540;

import lombok.RequiredArgsConstructor;

import java.nio.charset.Charset;

@RequiredArgsConstructor
@SuppressWarnings("unused")
public enum Charsets {
    CESU_8("CESU-8", "CESU8"),
    GB18030("GB18030", "GB18030"),
    IBM00858("IBM00858", "Cp858"),
    IBM437("IBM437", "Cp437"),
    IBM775("IBM775", "Cp775"),
    IBM850("IBM850", "Cp850"),
    IBM852("IBM852", "Cp852"),
    IBM855("IBM855", "Cp855"),
    IBM857("IBM857", "Cp857"),
    IBM862("IBM862", "Cp862"),
    IBM866("IBM866", "Cp866"),
    ISO_8859_1("ISO-8859-1", "ISO8859_1"),
    ISO_8859_13("ISO-8859-13", "ISO8859_13"),
    ISO_8859_15("ISO-8859-15", "ISO8859_15"),
    ISO_8859_16("ISO-8859-16", "ISO8859_16"),
    ISO_8859_2("ISO-8859-2", "ISO8859_2"),
    ISO_8859_4("ISO-8859-4", "ISO8859_4"),
    ISO_8859_5("ISO-8859-5", "ISO8859_5"),
    ISO_8859_7("ISO-8859-7", "ISO8859_7"),
    ISO_8859_9("ISO-8859-9", "ISO8859_9"),
    KOI8_R("KOI8-R", "KOI8_R"),
    KOI8_U("KOI8-U", "KOI8_U"),
    US_ASCII("US-ASCII", "ASCII"),
    UTF_16("UTF-16", "UTF-16"),
    UTF_16BE("UTF-16BE", "UnicodeBigUnmarked"),
    UTF_16LE("UTF-16LE", "UnicodeLittleUnmarked"),
    UTF_32("UTF-32", "UTF-32"),
    UTF_32BE("UTF-32BE", "UTF-32BE"),
    UTF_32LE("UTF-32LE", "UTF-32LE"),
    UTF_8("UTF-8", "UTF8"),
    WINDOWS_1250("windows-1250", "Cp1250"),
    WINDOWS_1251("windows-1251", "Cp1251"),
    WINDOWS_1252("windows-1252", "Cp1252"),
    WINDOWS_1253("windows-1253", "Cp1253"),
    WINDOWS_1254("windows-1254", "Cp1254"),
    WINDOWS_1257("windows-1257", "Cp1257"),
    X_IBM737("x-IBM737", "Cp737"),
    X_IBM874("x-IBM874", "Cp874"),
    X_UTF_16LE_BOM("x-UTF-16LE-BOM", "UnicodeLittle"),
    X_UTF_32BE_BOM("X-UTF-32BE-BOM", "X-UTF-32BE-BOM"),
    X_UTF_32LE_BOM("X-UTF-32LE-BOM", "X-UTF-32LE-BOM");

    final String nio;
    final String io;

    public static Charsets getDefault() {
        for (Charsets ch : Charsets.values()) {
            if (Charset.isSupported(ch.nio) && Charset.defaultCharset().name().equalsIgnoreCase(ch.nio)) {
                return ch;
            }
        }
        return Charsets.WINDOWS_1252;
    }

    public Charset toCharset() {
        return Charset.forName(nio);
    }

    @Override
    public String toString() {
        return nio;
    }
}
