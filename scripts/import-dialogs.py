#!/usr/bin/python
# -*- coding: utf-8 -*-

import sys, os
import time, datetime
import httplib2

baseUrl = 'http://localhost:9000'
http = httplib2.Http()

def parse(line) :
	columns = line.split('\t')
	timestamp = datetime.datetime.fromtimestamp(time.mktime(time.gmtime(long(columns[1])))).strftime('%Y-%m-%dT%H:%M:%S+08')
	return (columns[0], timestamp, columns[2], columns[3], columns[4], columns[5], columns[6], columns[7])

def getTags(tag, brand, model, accessory) : 
	tags = ''
	if tag != '' :
		tags = tag
	if brand != '' :
		tags = tags + ',' + brand
	if model != '' :
		tags = tags + ',' + model
	if accessory != '' :
		tags = tags + ',' + accessory
	return tags

def createDialog(username, tags, relationship) :
	topic = tags
	if tags == '':
		topic = 'Consultancy'

	params = '{"username": "'+username+'", "topic": "'+topic+'", "relationship": "'+relationship+'"}'
	headers = {'Content-type': 'application/json'}
	(response, content) = http.request('{0}/dialogs'.format(baseUrl), 'POST', headers=headers, body = params)
	location = response['location']
	return long(location[location.rfind('/') + 1 :])

def createMessage(username, currentDialogId, question, tags, timestamp) :
	params = '{"username": "'+username+'", "dialogId": '+str(currentDialogId)+', "content": "'+question+'", "tags": "'+tags+'", "timestamp": "'+timestamp+'"}'
	headers = {'Content-type': 'application/json'}
	(response, content) = http.request('{0}/messages'.format(baseUrl), 'POST', headers=headers, body = params)
	location = response['location']
	return long(location[location.rfind('/') + 1 :])

def replyMessage(messageId, currentDialogId, reply) :
	params = '{"username": "system", "dialogId": '+str(currentDialogId)+', "content": "'+reply+'"}'
	headers = {'Content-type': 'application/json'}
	http.request('{0}/messages/{1}/teach'.format(baseUrl, messageId), 'POST', headers=headers, body = params)

# main
dialog_file = sys.argv[1]
if not os.path.exists(dialog_file) :
	exit(0)
print dialog_file

currentUser = ''
currentDialogId = 0
for line in open(dialog_file).readlines()[1:] :
	(username, timestamp, question, reply, tag, brand, model, accessory) = parse(line)
	tags = getTags(tag, brand, model, accessory)
	print username + ", " + timestamp + ", " + question + ", " + reply + ", " + tags
	if question != '' :
		if currentUser != username :
			currentUser = username
			currentDialogId = createDialog(username, tags, 'Strangers')
			print 'create a dialog ' , str(currentDialogId)
		messageId = createMessage(username, currentDialogId, question, tags, timestamp)
		if reply != '' :
			replyMessage(messageId, currentDialogId, reply)


