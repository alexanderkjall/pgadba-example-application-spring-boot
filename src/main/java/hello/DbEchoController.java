package hello;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collector;
import jdk.incubator.sql2.AdbaType;
import jdk.incubator.sql2.DataSource;
import jdk.incubator.sql2.DataSourceFactory;
import jdk.incubator.sql2.Result.RowColumn;
import jdk.incubator.sql2.Session;
import jdk.incubator.sql2.Submission;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class DbEchoController {
    private final DataSource ds;

    public DbEchoController() {
        ds = DataSourceFactory.newFactory("org.postgresql.sql2.PgDataSourceFactory")
            .builder()
            .url("jdbc:postgresql://localhost:5432/test")
            .username("test")
            .password("test")
            .build();

    }

    private static <T> Collector<RowColumn, T[], T> singleCollector(Class<T> clazz) {
        return Collector.of(
            () -> (T[])new Object[1],
            (a, r) -> a[0] = r.at("t").get(clazz),
            (l, r) -> null,
            a -> a[0]);
    }


    @RequestMapping("/{val}")
    public CompletableFuture<String> index(@PathVariable("val") String val) {
        try (Session session = ds.getSession()) {
            Submission<String> sub = session.<String>rowOperation("select $1 as t")
                .set("$1", val, AdbaType.VARCHAR)
                .collect(singleCollector(String.class))
                .submit();

            return sub.getCompletionStage().toCompletableFuture();
        }
    }
}
