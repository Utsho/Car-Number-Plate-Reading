import numpy as np, cPickle, gzip, os
from PIL import Image
from matplotlib import pyplot as plt
from numpy import array

class DataSet:
	def __init__(self, images, labels, shape, classnames=None):
		n=len(images)
		assert n==len(labels)

		self.__samples__=n
		self.__classes__=max(labels)+1
		self.__FYI__=""
		self.__shape__=shape

		dx=np.array(range(n))
		np.random.shuffle(dx)

		self.images=np.array(images)[dx]
		self.labels=np.array(labels)[dx]

		if(classnames is None): self.classnames=np.array(range(self.__classes__))
		else: self.classnames=np.array(classnames)
	###

	def show(self, images, real, pred=None):
		assert(len(images)==len(real)==20 and (pred is None or len(pred)==20))

		fig,axes=plt.subplots(4,5, figsize=(8,6))
		fig.subplots_adjust(hspace=0.5, wspace=0.4)

		if(self.__shape__[-1]==3): shape=self.__shape__
		else: shape=self.__shape__[:-1]

		for i,ax in enumerate(axes.flat):
			ax.imshow(images[i].reshape(shape), cmap="binary")

			if(pred is None): xlabel="Real: {0}".format(self.classnames[real[i]])
			else: xlabel="Real: {0}, Pred: {1}".format(self.classnames[real[i]], self.classnames[pred[i]])

			ax.set_xlabel(xlabel)
			ax.set_xticks([])
			ax.set_yticks([])

		plt.show()
	###

	#def printInFiles(self, images, pred=None):
		
		#for i in pred:
			

	###
	def to_onehot(self, labels):
		out=[]
		for l in labels:
			temp=[0]*self.__classes__
			temp[l]=1
			out.append(temp)

		return np.array(out)
	###

	def next_batch(self, batch_size=50, offset=None):
		if(offset is None):
			dx=np.random.choice(len(self.images),size=batch_size,replace=False)
			return self.images,self.labels
		else:
			return self.images[offset:offset+batch_size],self.labels[offset:offset+batch_size]
	###

	def save(self,path):
		with gzip.open(path, "wb") as f: cPickle.dump(self, f)
	###

	def make_folds(self,fold=10):
		folds=[]

		for i in xrange(fold):
			st=i*self.__samples__/fold
			en=(i+1)*self.__samples__/fold

			img=np.append(self.images[:st],self.images[en:], axis=0)
			lbl=np.append(self.labels[:st],self.labels[en:], axis=0)
			train=DataSet(img, lbl, self.__shape__, self.classnames)

			img=self.images[st:en]
			lbl=self.labels[st:en]
			test=DataSet(img, lbl, self.__shape__, self.classnames)

			folds.append((train,test))

		return folds
	###

	@staticmethod
	def load(path):		
		with gzip.open(path, "rb") as f: return cPickle.load(f)
	###


	@staticmethod
	def readImageToPhoto(width,height,path):		
		img=Image.open(path)
		img=img.convert('L')
		pix=img.load()
		last=0
		count=0
		images2=[]
		n=0
		for j in range(4,img.size[1]):
			m=0
			if n==1:
				count=count+1
				if count>5:
					count=0
					n=0
				else: continue
			
			for i in range(img.size[0]):
				
				if pix[i,j]!=255:
					m=1
				
			if m==0:
				result=img.crop((0, last,img.size[0],j))
				
				result=result.resize((height,width), Image.ANTIALIAS)
				result=result.convert("1")
				
				
				result =np.array(result)
				
				result=result.reshape([-1])
				
				
				images2.append(result)
				if(j+10>img.size[1]):
					break
				last=j+5
				n=1
		return images2	
	###


	
	@staticmethod
	def prepare_from_folder(path, height, width):
		images=[]
		
		labels=[]
		classnames=os.listdir(path)
		channels=1;
		
		#print exmp1.size
		i23=0
		np.set_printoptions(threshold='nan')
		for label,temp in enumerate(classnames):
			folder=os.path.join(path,temp)
			print "Searching in "+temp
			
			for file in os.listdir(folder):
				with Image.open(os.path.join(folder,file)) as img:
					
					img=img.resize((height,width), Image.ANTIALIAS)
					img=img.convert("1")
					pix=img.load()
					
					#img=1.0-np.array(img)/255.0
					img =np.array(img)
					if i23==0:
						print '\n\n\n\n'

					img=img.reshape([-1])
					if i23==0:					
						print img
					images.append(img)
					labels.append(label)
					i23=i23+1		
					#print label
						

		if(len(images[0])/(height*width)==3): channels=3

		return DataSet(images, labels, (height, width, channels), classnames)
	###
###
