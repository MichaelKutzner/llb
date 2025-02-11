import java.awt.image.BufferedImage
import java.awt.Font
import java.awt.Color
import javax.imageio.ImageIO
import java.io.File
import java.awt.image.RenderedImage

@main def main(): Unit =
  val config = Config(500, "Hello", List("0xff0000", "0x00cc00", "0xbbbbbb"), "images")
  val letter_config =
    config.letters
      .zip(Iterator.continually(config.colors).flatten)
      .zipWithIndex
      .map(LetterConfig.New(_))
      .toList

  File(config.path).mkdirs()

  for (letter <- letter_config)
    val image = make_image(letter, config.size)
    write_image(image, s"${config.path}/${letter.index}.png")

case class Config (
  size: Int,
  letters: String,
  colors: List[String],
  path: String,
)

case class LetterConfig (
  letter: Char,
  color: String,
  index: Int,
)

object LetterConfig {
  def New(tuple: Any) =
    tuple match
      case ((letter: Char, color: String), index: Int) => LetterConfig(letter, color, index)
}

def make_image(config: LetterConfig, size: Int) =
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
  graphics.drawString(
    letter,
    ((canvas.getWidth() - box.getWidth()) / 2).toFloat,
    ((canvas.getHeight() + metrics.getAscent() - metrics.getDescent()) / 2).toFloat,
  )

  graphics.dispose()

  canvas


def write_image(image: RenderedImage, path: String) =
  ImageIO.write(image, "png", new File(path))


// def make_letter(letter: Char) =
//   val size = (500, 500)
//   val canvas = new BufferedImage(size._1, size._2, BufferedImage.TYPE_INT_RGB)
//   val graphics = canvas.createGraphics()

//   // Create border
//   graphics.setColor(Color.BLACK)
//   graphics.drawRect(0, 0, canvas.getWidth(), canvas.getHeight())
//   graphics.setColor(Color.WHITE)
//   graphics.fillRect(1, 1, canvas.getWidth() - 2, canvas.getHeight() - 2)

//   // Write letter
//   val font = Font("Arial", Font.PLAIN, (size._1 * 1.15).toInt)
//   graphics.setColor(Color.CYAN)
//   graphics.setFont(font)
//   val metrics = graphics.getFontMetrics()
//   val box = metrics.getStringBounds(letter.toString(), graphics)
//   graphics.drawString(
//     letter.toString(),
//     ((canvas.getWidth() - box.getWidth()) / 2).toFloat,
//     ((canvas.getHeight() + metrics.getAscent() - metrics.getDescent()) / 2).toFloat
//   )
//   // https://otfried.org/scala/drawing.html
//   // https://coderanch.com/t/753449/java/Font-metrics-centering-character-box
//   // // Write letter
//   // val font = Font("Arial", Font.PLAIN, (size._1 * 1.2).toInt)
//   // graphics.setColor(Color.CYAN)
//   // // graphics.setFont(font)
//   // graphics.setFont(new Font("Arial", Font.PLAIN, (size._1 / 0.77).toInt))
//   // // graphics.setFont(new Font("Arial", Font.CENTER_BASELINE, (size._1 / 0.77).toInt))
//   // val metrics = graphics.getFontMetrics()
//   // val box = metrics.getStringBounds(letter.toString(), graphics)
//   // // graphics.drawString(letter.toString(), ((canvas.getWidth() - box.getWidth()) / 2).toFloat, ((canvas.getHeight() + box.getHeight()) / 2).toFloat)
//   // graphics.drawString(
//   //   letter.toString(),
//   //   ((canvas.getWidth() - box.getWidth()) / 2).toFloat,
//   //   ((canvas.getHeight() + metrics.getAscent() - metrics.getDescent()) / 2).toFloat
//   //   // 499.toFloat
//   //   // ((canvas.getHeight() + font.getSize() - metrics.getDescent()) / 2).toFloat
//   //   // metrics.getAscent() - metrics.getDescent()
//   //   // ((canvas.getHeight() + metrics.getAscent() - metrics.getDescent()) / 2).toFloat
//   //   // ((canvas.getHeight() + metrics.getAscent()) / 2).toFloat
//   //   // ((canvas.getHeight() - box.getHeight()) / 2 + metrics.getAscent()).toFloat
//   //   // metrics.getAscent().toFloat
//   //   // ((canvas.getHeight() + 5 - box.getHeight()) / 2 + metrics.getAscent()).toFloat
//   //   // (box.getHeight() / 2 + metrics.getAscent()).toFloat,
//   //   // ((box.getHeight() + metrics.getAscent()) / 2).toFloat,
//   // )
//   // println(s"${canvas.getHeight()}, ${box.getHeight()}, ${metrics.getAscent()}")
//   // println(metrics)
//   // println(box)
//   // println((canvas.getHeight() + metrics.getAscent()) / 2)
//   // println(((canvas.getHeight() - box.getHeight()) / 2 + metrics.getAscent()).toFloat)
//   // println((canvas.getHeight() + metrics.getAscent()) / 2)
//   // println((canvas.getHeight() + metrics.getHeight()) / 2 - metrics.getDescent())
//   // println(((canvas.getHeight() + metrics.getAscent() - metrics.getDescent()) / 2).toFloat)
//   // println(metrics.getAscent() - metrics.getDescent())
//   // println(metrics.getAscent() - metrics.getDescent())
//   // println(font)
//   // println(font.getSize())
//   // // graphics.drawString(letter.toString(), 0, canvas.getHeight())

//   graphics.dispose()

//   canvas

// def write_letter(letter: RenderedImage, path: String) =
//   ImageIO.write(letter, "png", new File(path))
