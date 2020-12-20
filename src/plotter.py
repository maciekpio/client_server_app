
import matplotlib.pyplot as plt


def load_data(file_name):
	file = open(file_name)
	data_to_plot = []
	text = file.readlines()
	for string_repetitions_to_split in text[0].split('!'):
		string_repetitions = string_repetitions_to_split.split('|')
		repetitions = []
		for string_clients_results in string_repetitions[1].split('.'):
			clients_results = []
			for string_durations in string_clients_results.split(';'):
				durations = []
				for string_duration in string_durations.split(','):
					if(string_duration != ''):
							duration = int(string_duration)
					durations.append(duration)
				clients_results.append(durations)
			repetitions.append(clients_results)
		data_to_plot.append([string_repetitions[0], repetitions])
	file.close()
	return data_to_plot


def plot(input):
	data_to_plot = []
	for data_input1 in input:
		data = []
		for data_input2 in data_input1[1]:
			for data_input3 in data_input2:
				for data_input4 in data_input3:
					data.append(data_input4)
		data_to_plot.append(data)
	fig1, ax1 = plt.subplots()
	ax1.set_title('Basic Plot')
	ax1.boxplot(data_to_plot)
	fig1.savefig("plot.png",bbox_inches='tight')
	plt.show()


plot(load_data("experiences/_experience_0.txt"))