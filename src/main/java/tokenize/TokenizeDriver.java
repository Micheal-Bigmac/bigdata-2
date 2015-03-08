package tokenize;

import java.io.IOException;
import java.net.URI;

import inputformat.MyInputFormat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class TokenizeDriver {

	static String input = "hdfs://192.168.121.200:9000/digita";
	static String output = "hdfs://192.168.121.200:9000/digita/out";

	public static void main(String[] args) throws Exception {

		// set configuration
		Configuration conf = new Configuration();
		conf.setLong("mapreduce.input.fileinputformat.split.maxsize", 4000000); // max
																				// size
																				// of
																				// Split

		Job job = new Job(conf, "Tokenizer");
//		job.setJarByClass(TokenizeDriver.class);

		// specify input format
		job.setInputFormatClass(MyInputFormat.class);

		// specify mapper
		job.setMapperClass(TokenizeMapper.class);

		// specify output types
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		// specify input and output DIRECTORIES
		Path inPath = new Path(input);
		Path outPath = new Path(output);
		try { // input path
			FileSystem fs = FileSystem.get(new URI(input), conf);
			FileStatus[] stats = fs.listStatus(inPath);
			for (int i = 0; i < stats.length; i++)
				FileInputFormat.addInputPath(job, stats[i].getPath());
		} catch (IOException e1) {
		}
		FileOutputFormat.setOutputPath(job, outPath); // output path

	  // delete output directory
		try {
			FileSystem hdfs = FileSystem.get(new URI(output), conf);
			if (hdfs.exists(outPath))
				hdfs.delete(outPath);
			hdfs.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		// run the job
		System.exit(job.waitForCompletion(true) ? 0 : 1);

	}

}
