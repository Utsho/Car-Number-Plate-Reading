from CNN import CNN
from DataSet import *
import os
cwd = os.getcwd()


ds=DataSet.prepare_from_folder(cwd+"/dataset", height=40, width=40)


cnn=CNN(ds.__shape__, ds.__classes__)


cnn.train(ds, iteration=500,batch_size=64)

cnn.save('model')

