package ar.edu.utn.frba.dds.server.templates;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.rendering.FileRenderer;
import java.io.IOException;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class JavalinHandlebars implements FileRenderer {

  private final Handlebars handlebars;

  public JavalinHandlebars() {
    this.handlebars = new Handlebars();
    this.handlebars.registerHelper("formatDate", HandlebarsHelpers.formatDate());
    this.handlebars.registerHelper("eq", HandlebarsHelpers.eq());
    this.handlebars.registerHelper("json", HandlebarsHelpers.json());
  }

  @NotNull
  @Override
  public String render(
      @NotNull String path, @NotNull Map<String, ?> model,
      @NotNull Context context
  ) {
    Template template = null;
    try {
      template = handlebars.compile("templates/" + path.replace(".hbs", ""));
      return template.apply(model);
    } catch (IOException e) {
      context.status(HttpStatus.NOT_FOUND);
      return "No se encuentra la página indicada...";
    }
  }
}