curl -k -c /tmp/cookies.txt -b /tmp/cookies.txt --data 'username=thesmith&password=qweqweqwe' http://localhost:9000/users/register
curl -k -c /tmp/cookies.txt -b /tmp/cookies.txt --data 'userId=thesmith&domain=twitter&submit=Submit' http://localhost:9000/accounts/
curl -k -c /tmp/cookies.txt -b /tmp/cookies.txt --data 'userId=thesmith&domain=flickr&submit=Submit' http://localhost:9000/accounts/
curl -k -c /tmp/cookies.txt -b /tmp/cookies.txt --data 'userId=thesmith&domain=wordr&submit=Submit' http://localhost:9000/accounts/

