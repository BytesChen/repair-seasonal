import os
import matplotlib.pyplot as plt
import pandas as pd

for file in os.listdir("."):
    if file[0] != "_":
        continue
    fig = plt.figure()
    data = pd.read_csv("./" + file)
    val = data["value"]
    plt.plot(val)
    fig.savefig(file[1:-4] + '.jpg')
