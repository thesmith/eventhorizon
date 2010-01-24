curl -k -c /tmp/cookies.txt -b /tmp/cookies.txt --data 'username=thesmith&password=qweqweqwe' http://localhost:9000/users/register
curl -k -c /tmp/cookies.txt -b /tmp/cookies.txt --data 'userId=thesmith&submit=Submit' http://localhost:9000/accounts/twitter/
curl -k -c /tmp/cookies.txt -b /tmp/cookies.txt --data 'userId=thesmith&submit=Submit' http://localhost:9000/accounts/flickr/

