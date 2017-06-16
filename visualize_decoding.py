import turtle

filename = "decoded_wavepattern.txt"

def min(a, b):
    if (a < b):
        return a
    return b

def drawgrid():
    for i in range(int(len_vert / step_mark * 2)):
        a.forward(step_mark)
        a.left(90)
        a.forward(len_mark)
        a.left(180)
        a.forward(len_mark * 2)
        a.left(180)
        a.forward(len_mark)
        a.right(90)

a = turtle.Turtle()
a.shape("arrow")

turtle.screensize(1500, 1000)
turtle.getscreen().bgcolor("black")
a.pencolor("white")
len_vert = 490
len_hor = 1900
len_mark = 5
step_mark = 20
#draw coordinate system
a.speed(100)
a.pensize(1)
a.penup()
a.setpos(0 - len_hor / 2, 0)
a.pendown()
a.forward(len_hor)
a.stamp()
a.setpos(0 - len_hor / 2, 0)
a.left(90)
a.forward(len_vert)
a.stamp()
a.back(len_vert * 2)
#drawgrid()
a.setpos(0 - len_hor / 2, 0)
a.right(90)
#drawgrid()
file = open(filename, "r")
l = turtle.Turtle()
r = turtle.Turtle()
l.pencolor("white")
r.pencolor("white")
l.setpos(0 - len_hor / 2, 0)
r.setpos(0 - len_hor / 2, 0)
l.speed(100)
r.speed(100)
l.pencolor("red")
r.pencolor("green")

l.shape("triangle")
r.shape("triangle")
l.shapesize(0.3, 0.3, 0.3)
r.shapesize(0.3, 0.3, 0.3)

valueGap = 3
valuesPerLine = 8
numValues = int(len_hor / valueGap)

text = ""
for i in range(int(numValues / valuesPerLine + 1)):
    text += file.readline()
endidx = 0
for i in range(numValues):
   startidx = min(text.find("+", endidx), text.find("-", endidx))
   endidx = text.find("\t", startidx)
   l.setpos(i * valueGap - len_hor / 2, int(text[startidx:endidx]) / 2.5)
   startidx = min(text.find("+", endidx), text.find("-", endidx))
   endidx = text.find("\t", startidx)
   r.setpos(i * valueGap  - len_hor / 2, - int(text[startidx:endidx]) / 2.5)
   l.stamp()
   r.stamp()

file.close()       
mainloop()


