package common.time;

public class NtpQueryResult {
    public final long offsetMillis;
    public final long refTimeMillis;

    public NtpQueryResult(long offsetMillis, long refTimeMillis) {
        this.offsetMillis = offsetMillis;
        this.refTimeMillis = refTimeMillis;
    }

}
