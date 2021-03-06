package labkit_cluster.interactive;

import cz.it4i.parallel.TestParadigm;
import net.imglib2.labkit.LabkitFrame;
import net.imglib2.labkit.inputimage.InputImage;
import net.imglib2.labkit.inputimage.SpimDataInputImage;
import net.imglib2.labkit.models.DefaultSegmentationModel;
import org.scijava.Context;
import org.scijava.parallel.ParallelizationParadigm;

import java.io.File;

/**
 * Starts Labkit, with a special segmentation algorithm, that
 * connects to an ImageJ-Server to perform the segmentation.
 */
public class StartLabkit
{
	public static void main(String... args) {
		final String fiji = fileExists("/please/specify/path/to/your/fiji/installation/Fiji.app/ImageJ-linux64");
		// To run this example:
		// 1. FIJI needs to be installed on the computer.
		// 2. The jar file "imglib2-labkit-cluster-{version}.jar" generated by "mvn package" needs
		//    to be copied to the FIJI jar folder.
		final String filename = fileExists("/please/specify/path/to/big-data-viewer/dataset.xml");
		// To get a big data viewer dataset:
		// 1. Open and 3d gray scale image in FIJI. For example: File > Open Samples > T1 Head.
		// 2. Run: Plugins > BigDataViewer > Export Current Image as XML/HDF5.

		final Context context = new Context();
		final ParallelizationParadigm paradigm = TestParadigm.localImageJServer( fiji, context );
		final InputImage inputImage = new SpimDataInputImage( filename, 0 );
		DefaultSegmentationModel segmentationModel = new DefaultSegmentationModel( inputImage, context,
				( c, i ) -> new SciJavaParallelSegmenter( c, i, filename, paradigm ) );
		LabkitFrame.show(segmentationModel, "Demonstrate SciJava-Parallel used for Segmentation");

		Runtime.getRuntime().addShutdownHook( new Thread( paradigm::close ) );
	}

	private static String fileExists( String path )
	{
		if(!new File( path ).exists())
			throw new RuntimeException( "File doesn't exist: " + path );
		else
			return path;
	}
}
