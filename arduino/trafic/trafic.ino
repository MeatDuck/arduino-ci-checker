int incomingByte = 0; // for incoming serial data
unsigned long lastUpdate = 0;

void setup() {
  // Green
  pinMode(12, OUTPUT);

  // Yellow
  pinMode(11, OUTPUT);

  // Red
  pinMode(10, OUTPUT);

  Serial.begin(9600); // opens serial port, sets data rate to 9600 bps
}

void loop() {
  if (Serial.available() > 0) {
    incomingByte = Serial.read();
    lastUpdate = millis();
  }
  if (millis() > lastUpdate + 30 * 1000) {
    incomingByte = 'n';
  }
  
  if (incomingByte == 'g') {
    green();
  }
  else if (incomingByte == 'y') {
    yellow();
  }
  else if (incomingByte == 'r') {
    red();
  }
  else if (incomingByte == 'a') {
    all();
  }
}

void green() {
  digitalWrite(12, LOW);
  digitalWrite(11, LOW);
  digitalWrite(10, HIGH);
  delay(3000);
}

void yellow() {
  digitalWrite(12, LOW);
  digitalWrite(11, HIGH);
  digitalWrite(10, LOW);
  delay(3 * 1000);
}

void red() {
  digitalWrite(12, HIGH);
  digitalWrite(11, LOW);
  digitalWrite(10, LOW);
  delay(3 * 1000);
}

void all() {
  digitalWrite(12, HIGH);
  digitalWrite(11, HIGH);
  digitalWrite(10, HIGH);
  delay(3 * 1000);
}
