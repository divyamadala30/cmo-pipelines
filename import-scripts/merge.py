# ------------------------------------------------------------------------------
# imports

import sys
import itertools
import optparse
import os
import shutil
import re
import csv

# ------------------------------------------------------------------------------
# globals

# some file descriptors
ERROR_FILE = sys.stderr
OUTPUT_FILE = sys.stdout

NORMAL = "NORMAL"
PROFILE = "PROFILE"
MERGE_STYLES = {NORMAL:0, PROFILE:1}

OTHER_FILE = 'OTHER'

SEG_HG18_FILE_PATTERN = '_data_cna_hg18.seg'
SEG_HG18_META_PATTERN = '_meta_cna_hg18_seg.txt'

SEG_HG19_FILE_PATTERN = '_data_cna_hg19.seg'
SEG_HG19_META_PATTERN = '_meta_cna_hg19_seg.txt'

MUTATION_FILE_PATTERN = 'data_mutations_extended.txt'
MUTATION_META_PATTERN = 'meta_mutations_extended.txt'

CNA_FILE_PATTERN = 'data_CNA.txt'
CNA_META_PATTERN = 'meta_CNA.txt'

CLINICAL_FILE_PATTERN = 'data_clinical.txt'
CLINICAL_META_PATTERN = 'meta_clinical.txt'

LOG2_FILE_PATTERN = 'data_log2CNA.txt'
LOG2_META_PATTERN = 'meta_log2CNA.txt'

EXPRESSION_FILE_PATTERN = 'data_expression.txt'
EXPRESSION_META_PATTERN = 'meta_expression.txt'

FUSION_FILE_PATTERN = 'data_fusions.txt'
FUSION_META_PATTERN = 'meta_fusions.txt'

METHYLATION450_FILE_PATTERN = 'data_methylation_hm450.txt'
METHYLATION450_META_PATTERN = 'meta_methylation_hm450.txt'

METHYLATION27_FILE_PATTERN = 'data_methylation_hm27.txt'
METHYLATION27_META_PATTERN = 'meta_methylation_hm27.txt'

RPPA_FILE_PATTERN = 'data_rppa.txt'
RPPA_META_PATTERN = 'meta_rppa.txt'

TIMELINE_FILE_PATTERN = 'data_timeline.txt'
TIMELINE_META_PATTERN = 'meta_timeline.txt'

CLINICAL_PATIENT_FILE_PATTERN = 'data_clinical_patient.txt'
CLINICAL_PATIENT_META_PATTERN = 'meta_clinical_patient.txt'

CLINICAL_SAMPLE_FILE_PATTERN = 'data_clinical_sample.txt'
CLINICAL_SAMPLE_META_PATTERN = 'meta_clinical_sample.txt'

GENE_MATRIX_FILE_PATTERN = 'data_gene_matrix.txt'
GENE_MATRIX_META_PATTERN = 'meta_gene_matrix.txt'

SV_FILE_PATTERN = 'data_SV.txt'
SV_META_PATTERN = 'meta_SV.txt'

# we do not want to copy over or merge json files or meta_study.txt files
FILE_PATTERN_FILTERS = ['.json', 'meta_study.txt', '.orig']

NON_CASE_IDS = [
	'MIRNA', 
	'LOCUS', 
	'ID', 
	'GENE SYMBOL', 
	'ENTREZ_GENE_ID', 
	'HUGO_SYMBOL', 
	'LOCUS ID', 
	'CYTOBAND', 
	'COMPOSITE.ELEMENT.REF', 
	'HYBRIDIZATION REF',
]


# only files fitting patterns placed in these two lists will be merged
NORMAL_MERGE_PATTERNS = [MUTATION_META_PATTERN,
	FUSION_META_PATTERN,
	SEG_HG18_META_PATTERN,
	SEG_HG19_META_PATTERN,
	CLINICAL_META_PATTERN,
	CLINICAL_PATIENT_META_PATTERN,
	CLINICAL_SAMPLE_META_PATTERN,
	GENE_MATRIX_META_PATTERN, 
	SV_META_PATTERN,
	TIMELINE_FILE_PATTERN]

PROFILE_MERGE_PATTERNS = [CNA_META_PATTERN,
	LOG2_META_PATTERN,
	EXPRESSION_META_PATTERN,
	METHYLATION27_META_PATTERN,
	METHYLATION450_META_PATTERN,
	RPPA_META_PATTERN]

# Not everything in here is being used anymore, but mostly we want the map between meta and data files
META_FILE_MAP = {MUTATION_META_PATTERN:(MUTATION_FILE_PATTERN, 'mutations'),
	CNA_META_PATTERN:(CNA_FILE_PATTERN, 'cna'),
	LOG2_META_PATTERN:(LOG2_FILE_PATTERN, 'log2CNA'),
	SEG_HG18_META_PATTERN:(SEG_HG18_FILE_PATTERN, 'segment_hg18'),
	SEG_HG19_META_PATTERN:(SEG_HG19_FILE_PATTERN, 'segment_hg19'),
	METHYLATION27_META_PATTERN:(METHYLATION27_FILE_PATTERN, 'methylation_hm27'),
	METHYLATION450_META_PATTERN:(METHYLATION450_FILE_PATTERN, 'methylation_hm450'),
	FUSION_META_PATTERN:(FUSION_FILE_PATTERN, 'mutations'),
	RPPA_META_PATTERN:(RPPA_FILE_PATTERN, 'rppa'),
	EXPRESSION_META_PATTERN:(EXPRESSION_FILE_PATTERN, 'expression'),
	CLINICAL_META_PATTERN:(CLINICAL_FILE_PATTERN, 'clinical'),
	CLINICAL_PATIENT_META_PATTERN:(CLINICAL_PATIENT_FILE_PATTERN, 'clinical_patient'),
	CLINICAL_SAMPLE_META_PATTERN:(CLINICAL_SAMPLE_FILE_PATTERN, 'clinical_sample'),
	GENE_MATRIX_META_PATTERN:(GENE_MATRIX_FILE_PATTERN, 'gene_matrix'),
	SV_META_PATTERN:(SV_FILE_PATTERN, 'structural_variant'),
	TIMELINE_META_PATTERN:(TIMELINE_FILE_PATTERN, 'timeline')}

MUTATION_FILE_PREFIX = 'data_mutations_'

SEQUENCED_SAMPLES = []

# ------------------------------------------------------------------------------
# Functions

def merge_studies(file_types, sublist, output_directory, study_id, cancer_type):
	""" 
		Goes through all the potential file types and calls correct function for those types. 
		Normal merge, profile merge, and straight copy are the possibilities
	"""
	for file_type, files in file_types.items():
		if len(files) > 0:
			if file_type in META_FILE_MAP:
				# make sure there are data files in list so that empty files aren't generated
				if len(file_types[META_FILE_MAP[file_type][0]]) == 0:
					continue

				if len(files) > 1:
					print >> OUTPUT_FILE, 'Merging data associated with:'
					for f in files:
						print >> OUTPUT_FILE, '\t' + f
				else:
					print >> OUTPUT_FILE, 'Only one file found - Copying data associated with:\n\t' + files[0]

				# get merge style by file type
				if file_type in NORMAL_MERGE_PATTERNS:
					merge_style = MERGE_STYLES[NORMAL]
				else:
					merge_style = MERGE_STYLES[PROFILE]

				merge_files(files, file_types[META_FILE_MAP[file_type][0]], file_type, sublist, output_directory, merge_style, study_id)
			elif file_type == OTHER_FILE:
				# multiple studies may have the same file basename for other filetypes i.e., data_mutations_unfiltered.txt
				# we need to figure out which files to pair
				other_filetypes = {}
				for f in files:
					file_list = other_filetypes.get(os.path.basename(f), [])
					file_list.append(f)
					other_filetypes[os.path.basename(f)] = file_list

				# now that we have other filetypes paired, we will either copy the files to the output directory or merge them using the 'NORMAL' merge style
				files_to_copy = []
				for other_file_pattern,other_files in other_filetypes.items():
					if len(other_files) == 1:
						files_to_copy.append(other_files[0])
					else:
						print >> OUTPUT_FILE, 'Merging files matching "other" filename pattern:', other_file_pattern
						for f in other_files:
							print >> OUTPUT_FILE, '\t' + f
						merge_files('', other_files, file_type, sublist, output_directory, MERGE_STYLES[NORMAL], study_id)

				# copy files over to output directory if list not empty
				if len(files_to_copy) == 0:
					continue
				print >> OUTPUT_FILE, 'Copying other files meeting criteria from:'
				for f in files_to_copy:
					print >> OUTPUT_FILE, '\t' + f
				copy_files(files_to_copy, sublist, output_directory, study_id, cancer_type)

def merge_files(meta_filenames, data_filenames, file_type, sublist, output_directory, merge_style, study_id):
	""" 
		Merges files together by adding data from each file to a dictionary/list that contains all
		of the information. After all data from every file from a type is accumulated, it gets written
		out to a file.
	"""
	new_header = process_header(data_filenames, sublist, merge_style)

	if file_type in [SEG_HG18_META_PATTERN, SEG_HG19_META_PATTERN]:
		output_filename = os.path.join(output_directory, study_id + META_FILE_MAP[file_type][0])
	elif file_type == 'OTHER':
		output_filename = os.path.join(output_directory, os.path.basename(data_filenames[0]))
	else:
		output_filename = os.path.join(output_directory, META_FILE_MAP[file_type][0])		

	# key: String:gene
	# value: [(sample1Name,sample1Value),(sample1Name,sample2Value), ... ]
	gene_sample_dict = {}
	is_first_profile_datafile = True
	rows = []	
	
	for f in data_filenames:
		# update sequenced samples tag if data_mutations* file
		if MUTATION_FILE_PREFIX in f:
			update_sequenced_samples(f)

		# now merge data from file
		file_header = get_header(f)
		data_file = open(f, 'rU')
		lines = [l for l in data_file.readlines() if not l.startswith('#')][1:]

		# go through the lines, add the data to the objects
		for i,l in enumerate(lines):
			data = dict(zip(file_header, map(lambda x: process_datum(x), l.split('\t'))))
			if merge_style is MERGE_STYLES[PROFILE]:
				key = process_datum(l.split('\t')[0])
				if is_first_profile_datafile:
					# the new header will take care of any subsetting of the data when the file is written
					gene_sample_dict[key] = {k:v for k,v in data.items()}
				else:
					profile_row(key,data, new_header, gene_sample_dict)
			elif merge_style is MERGE_STYLES[NORMAL]:
				found = True
				if len(sublist) > 0:
					# determine whether sample id in values is in sublist
					if len([True for val in data.values() if val in sublist]) == 0:
						found = False
				if found:	
					rows.append(normal_row(data, new_header))
		is_first_profile_datafile = False
		data_file.close()

	# write out to the file
	if merge_style is MERGE_STYLES[PROFILE]:
		write_profile(gene_sample_dict, output_filename, new_header)
	if merge_style is MERGE_STYLES[NORMAL]:
		write_normal(rows, output_filename, new_header)
	
	print >> OUTPUT_FILE, 'Validating merge for: ' + output_filename
	validate_merge(data_filenames, output_filename, sublist, merge_style)

def validate_merge(data_filenames, merged_filename, sublist, merge_style):
	merged_file_summary = get_datafile_counts_summary(merged_filename, sublist, merge_style)
	merged_header = process_header(data_filenames, sublist, merge_style)

	# check that the merged header constructed from the data files matches the length of the header written to the merged file
	if len(merged_header) != merged_file_summary['num_cols']:
		print >> ERROR_FILE, 'Validation failed! Length of merged header does not match the header constructed from data files!'
		sys.exit(2)

	# get summary info for data filenames and compare with merged file counts
	datafile_summaries = {}
	total_rows = 0
	merged_gene_ids = []
	for filename in data_filenames:
		datafile_summaries[filename] = get_datafile_counts_summary(filename, sublist, merge_style)
		if merge_style is MERGE_STYLES[PROFILE]:
			new_gene_keys = [gene for gene in datafile_summaries[filename]['gene_ids'] if not gene in merged_gene_ids]
			merged_gene_ids.extend(new_gene_keys)
		else:
			total_rows += datafile_summaries[filename]['num_rows']
	# update total rows in merge style is profile
	if merge_style is MERGE_STYLES[PROFILE]:
		total_rows = len(merged_gene_ids)

	if total_rows != merged_file_summary['num_rows']:
		print >> ERROR_FILE, 'Validation failed! Total rows calculated from data files does not match total rows in merged file!'
		print datafile_summaries
		sys.exit(2)

	print 'Validation succeeded!\n'

def get_datafile_counts_summary(filename, sublist, merge_style):
	header = get_header(filename)
	data_file = open(filename, 'rU')
	filedata = [x for x in data_file.readlines() if not x.startswith('#')]

	# filter header if necessary
	if merge_style is MERGE_STYLES[PROFILE] and len(sublist) > 0:
		header = [hdr for hdr in header if hdr.upper() in NON_CASE_IDS or hdr in sublist]

	# figure out relevant row count
	gene_ids = [] # assuming that gene id is first column of profile data files
	if merge_style is MERGE_STYLES[PROFILE]:
		gene_ids = map(lambda x: process_datum(x.split('\t')[0]), filedata[1:])
		if len(sublist) > 0:
			num_rows = 0
			for row in filedata[1:]:
				if len([True for sid in sublist if sid in row]) > 0:
					num_rows += 1
		else:
			num_rows = len(filedata) - 1
	else:
		# if no sublist then number of rows is the total rows minus the header
		num_rows = len(filedata) - 1
	data_file.close()

	# fill summary info
	summary_info = {'num_cols':len(header), 'num_rows':num_rows, 'gene_ids':gene_ids}
	return summary_info

def update_sequenced_samples(filename):
	""" Updates the SEQUENCED_SAMPLES list. """
	data_file = open(filename, 'rU')
	comments = [x for x in data_file.readlines() if x.startswith('#')]
	for c in comments:
		if not 'sequenced_samples' in c:
			continue
		# split sequenced sample tag by all : and spaces, sample ids begin at index 1
		sequenced_samples = map(lambda x: process_datum(x), re.split('[: ]', c)[1:])
		SEQUENCED_SAMPLES.extend(sequenced_samples)
	data_file.close()

def process_datum(val):
	""" Cleans up datum. """
	try: 
		vfixed = val.strip()
	except AttributeError:
		vfixed = ''
	if vfixed in ['', 'NA', 'N/A', None]:
		return ''
	return vfixed

def get_header(filename):
	""" Gets the header from the file. """
	filedata = [x for x in open(filename).read().split('\n') if not x.startswith("#")]
	header = map(str.strip, filedata[0].split('\t'))
	return header

def process_header(data_filenames, sublist, merge_style):
	""" Handles header merging, accumulating all column names """
	header = []
	if merge_style is MERGE_STYLES[PROFILE]:
		for fname in data_filenames:			
			if len(sublist) > 0:
				new_header = []
				for hdr in [hdr for hdr in get_header(fname) if hdr not in header]:
					if hdr in NON_CASE_IDS or hdr in sublist:
						new_header.append(hdr)
				header.extend(new_header)
			else:
				new_header = [hdr for hdr in get_header(fname) if hdr not in header]
			header.extend(new_header)			
	elif merge_style is MERGE_STYLES[NORMAL]:
		for fname in data_filenames:
			new_header = [hdr for hdr in get_header(fname) if hdr not in header]
			header.extend(new_header)

	return header

def normal_row(line, header):
	""" 		
		Processes a normal merge style row. 
		A row is stored as a dictionary - key => column name, value => datum
	"""
	row = map(lambda x: process_datum(line.get(x, '')), header)

	return row	

def profile_row(key,data,header,gene_sample_dict):
	""" 
		Processes a profile merge style row.
		In this style, genes are the rows and data from each sample 
		must be associated to the correct gene. 
	"""

	# since we already know the new header, we just need to update the existing gene data with 
	# fields in the new header that aren't in the existing data yet
	# new_data = {k:v for k,v in data.items() if k in header and not k in existing_gene_data} 2:22
	existing_gene_data = gene_sample_dict.get(key, {})
	new_data = {k:v for k,v in data.items() if not k in existing_gene_data}
	existing_gene_data.update(new_data)
	gene_sample_dict[key] = existing_gene_data

	return gene_sample_dict

def write_profile(gene_sample_dict, output_filename, new_header):
	""" Writes out to file profile style merge data, gene by gene. """
	output_file = open(output_filename, 'w')
	output_file.write('\t'.join(new_header) + '\n')
	for gene,data in gene_sample_dict.iteritems():
		new_row = map(lambda x: process_datum(data.get(x, '')), new_header)
		output_file.write('\t'.join(new_row) + '\n')
	output_file.close()

def write_normal(rows, output_filename, new_header):
	""" Writes out to file normal style merge data, row by row. """
	output_file = open(output_filename, 'w')

	# if output file is data_mutations* then add sequenced samples tag to file before header
	if MUTATION_FILE_PREFIX in output_filename and SEQUENCED_SAMPLES != []:
		# only add unique sample ids - this shouldn't happen but done as a precaution
		output_file.write('#sequenced_samples: ' + ' '.join(list(set(SEQUENCED_SAMPLES))) + '\n')

	output_file.write('\t'.join(new_header) + '\n')
	for row in rows:
		output_file.write('\t'.join(row) + '\n')
	output_file.close()

def copy_files(filenames, sublist, output_directory, study_id, cancer_type):
	""" 
		Copies over files that aren't explicitly handled by this script (clinical, timeline, etc.). 
		If there are subsets involved, only copy those rows. Else just copy the file.
	"""
	count = 0
	for f in filenames:
		fname = os.path.basename(f)
		# these files should be unique so just keep the same file basename, only change output directory
		newfilename = os.path.join(output_directory, fname)
		
		# lets not bother with metafiles - gets tricky and not worth the trouble, probably would be more of a nuisance to do this anyway
		if 'meta' in fname:
			continue

		file_ok_to_copy = True
		if cancer_type != None:
			if cancer_type not in fname and fname != 'data_clinical.txt':
				file_ok_to_copy = False
		
		# do not copy files that already exist
		if os.path.isfile(os.path.join(output_directory, fname)):
			file_ok_to_copy = False

		if file_ok_to_copy:
			if 'clinical' in fname and len(sublist) > 0:
				count += 1
				make_subset(f,sublist,newfilename)
			elif 'timeline' in fname and len(sublist) > 0:
				count += 1
				make_subset(f,get_patient_id(sublist),newfilename)
			elif len(sublist) > 0:
				count += 1
				make_subset(f,sublist,newfilename)
			else:
				shutil.copy(f, newfilename)

def make_subset(filename, sublist, output_filename):
	""" Makes subset on files that are in normal format (sample per line) """
	try:
		subfile = open(filename, 'rU')
	except IOError:
		print >> ERROR_FILE, 'Error opening file'
		sys.exit(1)

	header = subfile.readline()
	output_file = open(output_filename, 'w')
	output_file.write(header)	

	for line in subfile.readlines():
		if len(sublist) == 0:
			output_file.write(line)
		else:
			[output_file.write(line) for x in sublist if x in line]
	subfile.close()
	output_file.close()

	# only write output file if number rows is greater than 1
	output_data = [line for line in open(output_filename, 'rU').readlines() if not line.startswith('#')]
	if len(output_data) <= 1:
		if len(sublist) > 0:
			print >> OUTPUT_FILE, 'Samples in sublist not found in file: ' + filename
			os.remove(output_filename)
		else:
			print >> ERROR_FILE, 'Error loading data from file: ' + filename
			sys.exit(2)

def get_patient_id(sublist):
	""" Get patient ids from sublist (MSKIMPACT ONLY) """
	patientid = []
	p = re.compile('(P-\d*)-T\d\d-[IM|TS|IH]+\d*')
	for sid in sublist:
		match = p.match(sid)
		if match:
			patientid.append(match.group(1))
		else:
			patientid.append(sid)
	return patientid

def organize_files(studies, file_types, merge_clinical):
	""" Put files in correct groups. Groups need to be explicitly defined by filenames, hence the ugly if else string. """
	for study in studies:
		study_files = [os.path.join(study,x) for x in os.listdir(study)]
		for study_file in study_files:

			# do not copy sub-directories in study path (i.e., case lists)
			skip_file = False
			if os.path.isdir(study_file):
				skip_file = True
			for filter_pattern in FILE_PATTERN_FILTERS:
				if filter_pattern in study_file:
					skip_file = True
			if skip_file:
				continue

			# META FILE PATTERN MATCHING
			if MUTATION_META_PATTERN in study_file:
				file_types[MUTATION_META_PATTERN].append(study_file)
			elif CNA_META_PATTERN in study_file:
				file_types[CNA_META_PATTERN].append(study_file)
			elif FUSION_META_PATTERN in study_file:
				file_types[FUSION_META_PATTERN].append(study_file)
			elif SEG_HG18_META_PATTERN in study_file:
				file_types[SEG_HG18_META_PATTERN].append(study_file)
			elif SEG_HG19_META_PATTERN in study_file:
				file_types[SEG_HG19_META_PATTERN].append(study_file)
			elif LOG2_META_PATTERN in study_file:
				file_types[LOG2_META_PATTERN].append(study_file)
			elif EXPRESSION_META_PATTERN in study_file:
				file_types[EXPRESSION_META_PATTERN].append(study_file)
			elif METHYLATION27_META_PATTERN in study_file:
				file_types[METHYLATION27_META_PATTERN].append(study_file)
			elif METHYLATION450_META_PATTERN in study_file:
				file_types[METHYLATION450_META_PATTERN].append(study_file)
			elif RPPA_META_PATTERN in study_file:
				file_types[RPPA_META_PATTERN].append(study_file)
			elif GENE_MATRIX_META_PATTERN in study_file:
				file_types[GENE_MATRIX_META_PATTERN].append(study_file)
			elif SV_META_PATTERN in study_file:
				file_types[SV_META_PATTERN].append(study_file)
			elif TIMELINE_META_PATTERN in study_file:
				file_types[TIMELINE_META_PATTERN].append(study_file)
			# FILE PATTERN MATCHING
			elif MUTATION_FILE_PATTERN in study_file:
				file_types[MUTATION_FILE_PATTERN].append(study_file)
			elif CNA_FILE_PATTERN in study_file:
				file_types[CNA_FILE_PATTERN].append(study_file)
			elif FUSION_FILE_PATTERN in study_file:
				file_types[FUSION_FILE_PATTERN].append(study_file)
			elif SEG_HG18_FILE_PATTERN in study_file:
				file_types[SEG_HG18_FILE_PATTERN].append(study_file)
			elif SEG_HG19_FILE_PATTERN in study_file:
				file_types[SEG_HG19_FILE_PATTERN].append(study_file)
			elif LOG2_FILE_PATTERN in study_file:
				file_types[LOG2_FILE_PATTERN].append(study_file)
			elif EXPRESSION_FILE_PATTERN in study_file:
				file_types[EXPRESSION_FILE_PATTERN].append(study_file)
			elif METHYLATION27_FILE_PATTERN in study_file:
				file_types[METHYLATION27_FILE_PATTERN].append(study_file)
			elif METHYLATION450_FILE_PATTERN in study_file:
				file_types[METHYLATION450_FILE_PATTERN].append(study_file)
			elif RPPA_FILE_PATTERN in study_file:
				file_types[RPPA_FILE_PATTERN].append(study_file)
			elif GENE_MATRIX_FILE_PATTERN in study_file:
				file_types[GENE_MATRIX_FILE_PATTERN].append(study_file)
			elif SV_FILE_PATTERN in study_file:
				file_types[SV_FILE_PATTERN].append(study_file)
			elif TIMELINE_FILE_PATTERN in study_file:
				file_types[TIMELINE_FILE_PATTERN].append(study_file)
			# CLINICAL FILE PATTERN MATCHING
			elif merge_clinical and 'clinical' in study_file:
				if CLINICAL_META_PATTERN in study_file:
					file_types[CLINICAL_META_PATTERN].append(study_file)
				elif CLINICAL_PATIENT_META_PATTERN in study_file:
					file_types[CLINICAL_PATIENT_META_PATTERN].append(study_file)
				elif CLINICAL_SAMPLE_META_PATTERN in study_file:
					file_types[CLINICAL_SAMPLE_META_PATTERN].append(study_file)
				elif CLINICAL_FILE_PATTERN in study_file:
					file_types[CLINICAL_FILE_PATTERN].append(study_file)
				elif CLINICAL_PATIENT_FILE_PATTERN in study_file:
					file_types[CLINICAL_PATIENT_FILE_PATTERN].append(study_file)
				elif CLINICAL_SAMPLE_FILE_PATTERN in study_file:
					file_types[CLINICAL_SAMPLE_FILE_PATTERN].append(study_file)
				else:
					file_types[OTHER_FILE].append(study_file)	
			else:
				file_types[OTHER_FILE].append(study_file)

def usage():
    print >> OUTPUT_FILE, 'merge.py --subset [/path/to/subset] --output-directory [/path/to/output] --study-id [study id] --cancer-type [cancer type] --merge-clinical [true/false] <path/to/study path/to/study ...>'

def main():
	""" Handle command line args, checks the directories exist, then calls passes things along to the other functions """
	
	# get command line stuff
	parser = optparse.OptionParser()
	parser.add_option('-s', '--subset', action = 'store', dest = 'subset')
	parser.add_option('-d', '--output-directory', action = 'store', dest = 'outputdir')
	parser.add_option('-i', '--study-id', action = 'store', dest = 'studyid')
	parser.add_option('-t', '--cancer-type', action = 'store', dest = 'cancertype')
	parser.add_option('-m', '--merge-clinical', action = 'store', dest = 'mergeclinical')

	(options, args) = parser.parse_args()

	subsetlist = options.subset
	output_directory = options.outputdir
	study_id = options.studyid
	cancer_type = options.cancertype
	merge_clinical = options.mergeclinical

	if (output_directory == None or study_id == None):
		usage()
		sys.exit(2)
	if subsetlist is not None:
		if not os.path.exists(subsetlist):
			print >> ERROR_FILE, 'ID list cannot be found: ' + subsetlist
			sys.exit(2)

	# check directories exist
	for study in args:
		if not os.path.exists(study):
			print >> ERROR_FILE, 'Study cannot be found: ' + study
			sys.exit(2)

	if not os.path.exists(output_directory):
		os.makedirs(output_directory)

	file_types = {MUTATION_FILE_PATTERN: [],
		CNA_FILE_PATTERN: [],
		FUSION_FILE_PATTERN: [],
		SEG_HG18_FILE_PATTERN: [],
		SEG_HG19_FILE_PATTERN: [],
		LOG2_FILE_PATTERN: [],
		EXPRESSION_FILE_PATTERN: [],
		METHYLATION27_FILE_PATTERN: [],
		METHYLATION450_FILE_PATTERN: [],
		RPPA_FILE_PATTERN: [],
		GENE_MATRIX_FILE_PATTERN: [],
		SV_FILE_PATTERN: [],
		TIMELINE_FILE_PATTERN: [],
		MUTATION_META_PATTERN: [],
		CNA_META_PATTERN: [],
		FUSION_META_PATTERN: [],
		SEG_HG18_META_PATTERN: [],
		SEG_HG19_META_PATTERN: [],		
		LOG2_META_PATTERN: [],
		EXPRESSION_META_PATTERN: [],
		METHYLATION27_META_PATTERN: [],
		METHYLATION450_META_PATTERN: [],
		RPPA_META_PATTERN: [],
		GENE_MATRIX_META_PATTERN: [],
		SV_META_PATTERN: [],
		TIMELINE_META_PATTERN: [],
		OTHER_FILE: []}

	# adds clinical file types if merge_clinical is true
	if merge_clinical != None and merge_clinical.lower() == 'true':
		merge_clinical = True
		file_types[CLINICAL_META_PATTERN] = []
		file_types[CLINICAL_PATIENT_META_PATTERN] = []
		file_types[CLINICAL_SAMPLE_META_PATTERN] = []		
		file_types[CLINICAL_FILE_PATTERN] = []
		file_types[CLINICAL_PATIENT_FILE_PATTERN] = []
		file_types[CLINICAL_SAMPLE_FILE_PATTERN] = []
	else:
		merge_clinical = False

	# get all the filenames
	organize_files(args, file_types, merge_clinical)

	#subset list
	sublist = []
	if subsetlist is not None:	
		sublist = [line.strip() for line in open(subsetlist,'rU').readlines()]

	# merge the studies
	merge_studies(file_types, sublist, output_directory, study_id, cancer_type)

# do the main
if __name__ == '__main__':
	main()