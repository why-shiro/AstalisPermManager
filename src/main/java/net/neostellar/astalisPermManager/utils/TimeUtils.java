package net.neostellar.astalisPermManager.utils;

public class TimeUtils {
    public static long parseDuration(String input) {
        input = input.toLowerCase();
        long multiplier;

        if (input.endsWith("d")) {
            multiplier = 86400000L; // 1 gün = 86400 saniye = 86400000 ms
        } else if (input.endsWith("h")) {
            multiplier = 3600000L; // 1 saat = 3600 saniye = 3600000 ms
        } else if (input.endsWith("m")) {
            multiplier = 60000L; // 1 dakika = 60 saniye = 60000 ms
        } else if (input.endsWith("s")) {
            multiplier = 1000L;
        } else {
            throw new IllegalArgumentException("Geçersiz zaman birimi.");
        }

        String numberPart = input.substring(0, input.length() - 1);
        long number = Long.parseLong(numberPart);

        return number * multiplier;
    }

    public static String formatDuration(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) return days + " gün";
        if (hours > 0) return hours + " saat";
        if (minutes > 0) return minutes + " dakika";
        return seconds + " saniye";
    }

}
