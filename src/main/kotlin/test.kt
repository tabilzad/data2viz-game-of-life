import io.data2viz.color.Color
import io.data2viz.color.Colors
import io.data2viz.force.forceSimulation
import io.data2viz.geom.Point
import io.data2viz.geom.point
import io.data2viz.geom.size
import io.data2viz.math.Angle
import io.data2viz.math.pct
import io.data2viz.math.toRadians
import io.data2viz.random.RandomDistribution
import io.data2viz.viz.bindRendererOn
import io.data2viz.viz.viz

class test {

    fun test() {
        val uniform = RandomDistribution.uniform(2.0, 25.0)

        val randPosx = RandomDistribution.uniform(.0, vizSize * 2)
        val randPosy = RandomDistribution.normal(.0, vizSize)
        val colorRand = RandomDistribution.uniform(.0, 255.0)
        val binaryRand = RandomDistribution.uniform(-20.0, 45.0)
        val randomStrength = RandomDistribution.uniform(-7.0, 5.0)

        val points = (0..50).map { point(randPosx(), 1000.0) }.toMutableList() // particles birth

        val speed = 5
        val simulation1 = forceSimulation<Point> {// add particles to simulation container
            domainObjects = points

            initForceNode = {
                //points.add(point(randPosx(), randPosy()))
                x = points[index].x
                y = points[index].y
            }

            intensityMin = 0.001.pct
            intensityDecay = 0.002.pct

            forceY {
                yGet = { -100.0 }
                strengthGet = { 2.pct * uniform().pct }
            }

        }
        val a = Angle(binaryRand())
        viz {
            size = size(vizSize * 2, vizSize)


            val circles = points.map {
                circle {
                    radius = uniform()
                    fill = Colors.rgb(colorRand().toInt(), colorRand().toInt(), colorRand().toInt())
                    x = it.x
                    y = it.y
                }
            }.toMutableList()

            animation {// updates frames 60fps loop

                simulation1.domainObjects = points
                simulation1.nodes.forEach { forceNode ->
                    circles[forceNode.index].apply {
                        x = forceNode.x
                        y = forceNode.y
                        fill = Colors.hsl(
                            (fill as Color).toHsl().h + Angle(binaryRand().toRadians()) / 10,
                            ((fill as Color).toHsl().s),
                            (fill as Color).toHsl().l
                        )
                    }

                    if (forceNode.y > 50) {
                        // circles.removeAt(forceNode.index)
                    }

//                if (RandomDistribution.uniform(0.0, 100.0)() < 20) {
//                    circles.add(circle {
//                        x = vizSize / 2
//                        y = vizSize / 2
//                        fill = Colors.Web.black
//                    })
//                }
//                if (forceNode.x > vizSize) {
//                    forceNode.x = (forceNode.x % vizSize)
//                }
//                if (forceNode.y.absoluteValue > vizSize) {
//                    forceNode.y = -(forceNode.y.absoluteValue % vizSize)
//                }
                }
            }
        }.bindRendererOn("viz")
    }
}