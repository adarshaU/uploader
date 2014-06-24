import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class MaxTemperature extends Configured implements Tool {

	public static class MapMapper extends
			Mapper<LongWritable, Text, Text, PairWritable> {

		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String regex = ",";// ''single quote not applicable for comma.
			String[] val = value.toString().split(regex);

			FloatWritable[] vv = new FloatWritable[2];
			vv[0] = new FloatWritable(Float.parseFloat(val[3]));
			vv[1] = new FloatWritable(Float.parseFloat(val[13]));

			PairWritable ddd = new PairWritable();
			context.write(new Text(val[2]), ddd.set(vv[0], vv[1]));

		}

	}
	
/*
 * extends array writable is optional 
	public static class PairWritable extends ArrayWritable implements Writable {

		public PairWritable() {
			super(FloatWritable.class);
			// TODO Auto-generated constructor stub
		}
*/
	

	public static class PairWritable extends ArrayWritable implements Writable {

		public PairWritable() {
			super(FloatWritable.class);
			// TODO Auto-generated constructor stub
		}

		private FloatWritable floatone;
		private FloatWritable floattwo;

		public String toString() {

			String s = Float.toString(floatone.get());
			String a = Float.toString(floattwo.get());
			String q = s + '\t' + a;
			return q;
		}

		public PairWritable set(float f1, float f2) {
			FloatWritable ff1 = new FloatWritable(f1);
			FloatWritable ff2 = new FloatWritable(f2);
			set(ff1, ff2);
			return this;
		}

		public PairWritable set(FloatWritable f1, FloatWritable f2) {
			this.floatone = f1;
			this.floattwo = f2;
			return this;
		}

		public float getone() {
			return floatone.get();
		}

		public float gettwo() {
			return floattwo.get();
		}

		public void write(DataOutput out) throws IOException {
			out.writeFloat(floatone.get());
			out.writeFloat(floattwo.get());
		}

		public void readFields(DataInput in) throws IOException {
			floatone = new FloatWritable(in.readFloat());
			floattwo = new FloatWritable(in.readFloat());
		}

	}

	public static class Mapreducers extends
			Reducer<Text, PairWritable, Text, PairWritable> {

		public void reduce(Text key, Iterable<PairWritable> values,
				Context context) throws IOException, InterruptedException {

			float sumone = 0;
			float sumtwo = 0;

			for (PairWritable dd : values) {
				sumone += dd.getone();
				sumtwo += dd.gettwo();

			}
			PairWritable ddd = new PairWritable();
			context.write(key, ddd.set(sumone, sumtwo));

		}

	}

	public int run(String[] args) throws Exception {
		Job job = new Job();
		job.setJarByClass(MaxTemperature.class);
		job.setJobName("MaxTemperature");

		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(URI.create(args[0]), conf);

		if (fs.exists(new Path(args[1]))) {
			fs.delete(new Path(args[1]), true);
		}

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setMapperClass(MapMapper.class);
		job.setCombinerClass(Mapreducers.class);
		// job.setNumReduceTasks(0);
		job.setReducerClass(Mapreducers.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(PairWritable.class);

		return job.waitForCompletion(true) ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		int xx = 1;
		xx = ToolRunner.run(new MaxTemperature(), args);
		System.exit(xx);
	}

}
