package bgp.he.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by tailong on 2015/6/17.
 */
public class DNS implements PageProcessor {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private BufferedWriter bw = null;
    private Site site = Site.me()
            .setRetryTimes(3)
            .setSleepTime(1000)
            .setTimeOut(10000)
            .addHeader("accept-encoding", "gzip, deflate")
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0")
            .addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");

    public void process(Page page) {


        if (page.getUrl().toString().contains("search")) {
            for (String s : page.getHtml().xpath("//table//a/text()").all()) {
                if (!s.contains("AS")) {
                    logger.info(s);
                    write(s);
                }
            }
            close();
        }

        if (page.getUrl().toString().contains("_ipinfo")) {
            for (String s : page.getHtml().xpath("//*[@id='ipinfo']/a/text()").all()) {
                if (s.contains("/")) {
                    logger.error(s);
                    write(s);
                }
            }
            close();
        }
    }

    public Site getSite() {
        return site;
    }

    private void write(String s) {
        try {
            bw = new BufferedWriter(new FileWriter(new File("e://ip"), true));
            bw.write(s);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        try {
            this.bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String[] company = new String[]{"http://bgp.he.net/search?search[search]=Google+Inc",
                "http://bgp.he.net/search?search[search]=Twitter+Inc",
                "http://bgp.he.net/search?search[search]=facebook",

        };
        Spider.create(new DNS())
                .addUrl(company)
                .addPipeline(new ConsolePipeline())
                .run();
    }
}
