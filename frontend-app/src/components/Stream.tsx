import React, { useEffect } from "react";

interface Tweet {
  message: string;
}

function TweetStream() {
  const [tweets, setTweets] = React.useState<Tweet[]>([]);

  useEffect(() => {
    const eventSource = new EventSource("http://localhost:8083/tweets");

    eventSource.onmessage = (event) => {
      if (event.data) {
        try {
          const tweet = JSON.parse(event.data);
          setTweets((tweets) => [tweet, ...tweets].slice(0, 10));
        } catch (error) {
          console.error("Error parsing JSON:", event.data, error);
        }
      }
    };

    return () => {
      eventSource.close();
    };
  }, []);

  return (
    <div>
      <h1> Stream</h1>
      {tweets.map((tweet, index) => (
        <p key={index}>{tweet.message}</p>
      ))}
    </div>
  );
}

export default TweetStream;
