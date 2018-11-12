package no.hackeriet;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collector;
import java.util.stream.Collectors;
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
    private final Session session;

    public DbEchoController() {
        ds = DataSourceFactory.newFactory("org.postgresql.adba.PgDataSourceFactory")
            .builder()
            .url("jdbc:postgresql://localhost:5432/test")
            .username("test")
            .password("test")
            .build();

        session = ds.getSession();
    }

    @RequestMapping("/{val}")
    public CompletableFuture<String> index(@PathVariable("val") String val) {
        Submission<List<RowColumn>> sub = session.<List<RowColumn>>rowOperation("select $1 as t")
            .set("$1", val, AdbaType.VARCHAR)
            .collect(Collectors.toList())
            .submit();

        return sub.getCompletionStage().thenApply(rc -> rc.get(0).at("t").get(String.class)).toCompletableFuture();
    }
}
