import java.awt.{Font, Color}
import java.awt.image.{BufferedImage, RenderedImage}
import java.io.File
import javax.imageio.ImageIO

@main def main(): Unit =
  val config =
    Config(500, "Hello", List("0xff0000", "0x00cc00", "0xbbbbbb"), "images")
  File(config.path).mkdirs()

  val letter_configs =
    config.letters
      .zip(Iterator.continually(config.colors).flatten)
      .zipWithIndex
      .map(LetterConfig.New(_))
  for (letter_config <- letter_configs)
    val image_path = s"${config.path}/${letter_config.index}.png"
    val image = make_image(letter_config, config.size)
    write_image(image, image_path)

case class Config(
    size: Int,
    letters: String,
    colors: List[String],
    path: String
)

case class LetterConfig(
    letter: Char,
    color: String,
    index: Int
)

object LetterConfig {
  def New(tuple: Any) =
    tuple match
      case ((letter: Char, color: String), index: Int) =>
        LetterConfig(letter, color, index)
}

def make_image(config: LetterConfig, size: Int) =
  // See also: https://otfried.org/scala/drawing.html
  // https://coderanch.com/t/753449/java/Font-metrics-centering-character-box

  val letter = config.letter.toString()
  val canvas = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB)
  val graphics = canvas.createGraphics()

  // Create white canvas with black border
  graphics.setColor(Color.BLACK)
  graphics.drawRect(0, 0, canvas.getWidth(), canvas.getHeight())
  graphics.setColor(Color.WHITE)
  graphics.fillRect(1, 1, canvas.getWidth() - 2, canvas.getHeight() - 2)

  // Write letter
  val font = Font("Arial", Font.PLAIN, (size * 1.15).toInt)
  graphics.setColor(Color.decode(config.color))
  graphics.setFont(font)
  val metrics = graphics.getFontMetrics()
  val box = metrics.getStringBounds(letter, graphics)
  val width = ((canvas.getWidth() - box.getWidth()) / 2).toFloat
  val height = ((canvas.getHeight() + metrics.getAscent() - metrics
    .getDescent()) / 2).toFloat
  graphics.drawString(letter, width, height)

  graphics.dispose()

  canvas

def write_image(image: RenderedImage, path: String) =
  ImageIO.write(image, "png", new File(path))
