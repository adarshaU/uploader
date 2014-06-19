
import java.io.IOException;
import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class MaxTemperature extends Configured implements Tool {

	public static class MapMapper extends
			Mapper<LongWritable, Text, Text, IntWritable> {
		private static final int MISSING = 9999;
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			int airTemp;
			String val = value.toString();
			String year = val.substring(15, 19);
			if(val.charAt(87)=='+'){
				airTemp = Integer.parseInt(val.substring(88, 92));
				
			}else{
				airTemp = Integer.parseInt(val.substring(87, 92));
			}
			 String quality = val.substring(92, 93);		
			if (airTemp != MISSING && quality.matches("[01459]")) {
			context.write(new Text(year), new IntWritable(airTemp));
			}
		}

	}

	

	public int run(String[] args) throws Exception {
		Job job = new Job();
		job.setJarByClass(MaxTemperature.class);
		job.setJobName("max temperature");
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(URI.create(args[0]), conf);
		if (fs.exists(new Path(args[1]))) {
			fs.delete(new Path(args[1]), true);
		}

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setMapperClass(MapMapper.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		

		return job.waitForCompletion(true) ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		int xx = 1;
		xx = ToolRunner.run(new MaxTemperature(), args);
		System.exit(xx);
	}

}
