
def build_lexicon(file_name):
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

print(build_lexicon("experiences/_experience_0.txt"))
