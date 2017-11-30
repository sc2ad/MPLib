import matplotlib.pyplot as plt
from matplotlib import gridspec
import sys
import pandas as pd


paths = sys.argv.copy()
del paths[0]
count = len(paths)

gs = gridspec.GridSpec(count, 1)
last_ax = None

for i, path in enumerate(paths):
	df = pd.read_csv(path, error_bad_lines=False)

	time = df['times']
	speeds = df['speeds']
	positions = df['positions']
	accelerations = df['accelerations']
	print(accelerations)

	ax = plt.subplot(gs[i], sharex=last_ax)
	ax.set_title(path)
	ax.set_xlabel('time (s)')
	ax.set_ylabel('units')
	line_speed, line_accel, line_pos = ax.plot(time, speeds, 'g', time, accelerations, 'r', time, positions, 'b')

	ax.legend((line_speed, line_accel, line_pos), ('speed', 'acceleration', 'position'), loc='lower right')
	last_ax = ax

plt.show()