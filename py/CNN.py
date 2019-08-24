import os
os.environ['TF_CPP_MIN_LOG_LEVEL']='3'
import tensorflow as tf, prettytensor as pt, time, os
from datetime import timedelta

class CNN:
	def __init__(self, image_shape, class_count, conv2d_layers=[(5,16),(5,36)], fc_layers=[128]):
		self.image_shape=image_shape
		self.class_count=class_count
		self.conv2d_layers=conv2d_layers
		self.fc_layers=fc_layers

		image_height=image_shape[0]
		image_width=image_shape[1]
		image_channels=image_shape[2]

		x_input=tf.placeholder(tf.float32, shape=[None, image_height*image_width*image_channels], name='x')
		x=tf.reshape(x_input, [-1, image_height, image_width, image_channels])

		y_input=tf.placeholder(tf.float32, shape=[None, class_count], name='y')
		y_real=tf.argmax(y_input, dimension=1)

		with pt.defaults_scope(activation_fn=tf.nn.relu):
			temp=pt.wrap(x)

			for filter_size,filter_count in conv2d_layers:
				temp.conv2d(kernel=filter_size, depth=filter_count).max_pool(kernel=2, stride=2)

			temp=temp.flatten()

			for fc_size in fc_layers:
				temp=temp.fully_connected(size=fc_size)

			y_layer,cost=temp.softmax_classifier(num_classes=class_count, labels=y_input)
			y_pred=tf.argmax(y_layer, dimension=1)
		
		self.x_input=x_input
		self.y_input=y_input
		self.y_pred=y_pred
		self.optimizer=tf.train.AdamOptimizer(1e-4).minimize(cost)
		self.accuracy=tf.reduce_mean(tf.cast(tf.equal(y_real,y_pred), tf.float32))

		self.session=tf.Session()
		self.session.run(tf.global_variables_initializer())
	###

	def __del__(self):
		self.session.close()
	###

	def train(self, data, iteration=100, batch_size=64):
		start_time=time.time();

		print ""
		print "Training with batch size {0}.".format(batch_size)
		print ""

		for i in xrange(iteration):
			x_input,y_input=data.next_batch(batch_size)
			feed_dict={self.x_input: x_input, self.y_input: data.to_onehot(y_input)}
			#print feed_dict
			##print data.next_batch(batch_size)
			##print  data.to_onehot(y_input)
			##print x_input
			self.session.run(self.optimizer, feed_dict)
				

			if((i+1)%50==0 or 1==1):
				print "Accuracy on training set after {0}th iteration: {1}".format(i+1, self.test(data, batch_size))

		print "Training completed."
		print "Time elapsed: {0}".format(timedelta(seconds=int(round(time.time()-start_time))))
		print ""
	###

	def trainWithFold(self, li, iteration=100, batch_size=64):
		start_time=time.time();

		print ""
		print "Training with batch size {0}.".format(batch_size)
		print ""
		k=0

		for i in xrange(200):
			x_input,y_input=li[0].next_batch(batch_size)
			feed_dict={self.x_input: x_input, self.y_input: li[0].to_onehot(y_input)}
			#print feed_dict
			##print data.next_batch(batch_size)
			##print  data.to_onehot(y_input)
			##print x_input
			self.session.run(self.optimizer, feed_dict)
			

			if((i+1)%100==0 or 1==1):
				print "Accuracy on training set after {0}th iteration: {1}".format(i+1, self.test(li[1], batch_size))

				k=self.test(li[1], batch_size)


		print "Training completed."
		print "Time elapsed: {0}".format(timedelta(seconds=int(round(time.time()-start_time))))
		print ""
		return k


	###

	def predict(self, x_input):
		return self.session.run(self.y_pred, {self.x_input: x_input})
	###

	def save(self, path):
		try:
			#os.makedirs(path)
			loc=tf.train.Saver().save(self.session, os.path.join(path,"model.ckpt"))
			print "Trained model is stored at: "+loc

		except OSError:
			if(os.path.isdir(path) is False): print "{0} is not a directory.".format(path)
			else: 
				print "Failed to store the model to disk."
				print OSError
	###

	def load(self, path):


		ckpt = tf.train.get_checkpoint_state('model')
      	        
      		tf.train.Saver().restore(self.session, ckpt.model_checkpoint_path)
     
		#path=os.path.join(path, "checkpoint")

		'''if(os.path.exists(path)):
			tf.train.Saver().restore(self.session, path)
			print "Model is restored."
		else: 
			print "The specified path does not exist."'''
	###

	def test(self, data, batch_size=None):	# Returns accuracy calculated using the parameter 'data'
		if(batch_size is None): x_input,y_input=data.images,data.labels
		else: x_input,y_input=data.next_batch(batch_size)
		
		return self.session.run(self.accuracy, {self.x_input: x_input, self.y_input: data.to_onehot(y_input)})
	###
###
