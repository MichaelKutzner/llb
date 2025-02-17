import java.awt.{Font, GraphicsEnvironment, Color}
import java.awt.image.{BufferedImage, RenderedImage}
import java.io.File
import javax.imageio.ImageIO

import scala.util.{Try, Success, Failure}

@main def main(): Unit =
  val config = read_config("config.json")
  File(config.path).mkdirs()

  val letter_configs =
    config.letters
      .zip(Iterator.continually(config.colors).flatten)
      .map(Letter.apply)
  for ((letter: Letter, index: Int) <- letter_configs.zipWithIndex)
    val image = letter.make_image(config)
    val image_path = s"${config.path}/${index}.png"
    write_image(image, image_path)

  // Available fonts
  // println(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames().toList)

case class Config(
    size: Int,
    letters: String,
    font: Font,
    adjust_y: Int,
    colors: List[Color],
    path: String
)

def read_config(path: String) =
  val json = ujson.read(os.read(os.pwd / path))

  val size = Try(json("size").num.toInt).getOrElse(400)

  Config(
    size,
    Try(json("text").str).getOrElse("LLB"),
    Font(
      Try(json("font").str).getOrElse("Monospaced"),
      Font.PLAIN,
      (size * 1.15).toInt
    ),
    Try(json("adjust_y").num.toInt).getOrElse(0),
    Try(json("colors").arr.map(_.str).toList)
      .getOrElse(List("magenta", "0xff00ff", "fuchsia"))
      .map(_.toColor),
    Try(json("path").str).getOrElse("images")
  )

case class Letter(
    letter: Char,
    color: Color
) {
  def make_image(config: Config) =
    // See also: https://otfried.org/scala/drawing.html
    // https://coderanch.com/t/753449/java/Font-metrics-centering-character-box

    val text = letter.toString()
    val canvas =
      new BufferedImage(config.size, config.size, BufferedImage.TYPE_INT_RGB)
    val graphics = canvas.createGraphics()

    // Create white canvas with black border
    graphics.setColor(Color.BLACK)
    graphics.drawRect(0, 0, canvas.getWidth(), canvas.getHeight())
    graphics.setColor(Color.WHITE)
    graphics.fillRect(1, 1, canvas.getWidth() - 2, canvas.getHeight() - 2)

    // Write letter
    graphics.setColor(color)
    graphics.setFont(config.font)
    val metrics = graphics.getFontMetrics()
    val box = metrics.getStringBounds(text, graphics)
    val width = ((canvas.getWidth() - box.getWidth()) / 2).toFloat
    val height = ((canvas.getHeight() + metrics.getAscent() - metrics
      .getDescent()) / 2).toFloat + config.adjust_y
    graphics.drawString(text, width, height)

    // Finish image
    graphics.dispose()

    canvas

}

extension (color: String)
  def toColor =
    Try(Color.decode(color)).getOrElse(Color.getColor(color, Color(0x778899)))

def write_image(image: RenderedImage, path: String) =
  ImageIO.write(image, "png", new File(path))
